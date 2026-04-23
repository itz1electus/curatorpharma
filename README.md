# curatorpharma

`curatorpharma` is a Kotlin/Ktor backend for a small pharmaceutical commerce flow. It exposes catalog, cart, checkout, order, and health endpoints, persists cart and order data in PostgreSQL through Exposed, and serves a lightweight static test console from the same process.

This README is written from the code as it exists today, including current limitations and rough edges that matter during onboarding.

## Tech stack

- Kotlin `2.3.0`
- Ktor `3.4.2`
- Exposed `1.0.0-rc-4`
- PostgreSQL JDBC `42.7.8`
- HikariCP `6.3.0`
- Logback `1.5.18`
- Java toolchain `25`
- Gradle Kotlin DSL

## What the service does

The API currently supports:

- Listing catalog products
- Fetching a single product
- Managing a demo user's cart
- Converting the cart into an order
- Fetching a single order
- Basic process health checking

The root path `/` serves a static HTML/JS page under `src/main/resources/static/` that exercises the API manually.

## Architecture

The code follows a straightforward layered backend structure:

- `src/main/kotlin/Application.kt`
  - Application bootstrap
  - Database initialization
  - Ktor plugin installation
  - Repository and service wiring
- `src/main/kotlin/routes/`
  - HTTP route definitions
- `src/main/kotlin/service/`
  - Thin application services delegating to repositories
- `src/main/kotlin/repository/`
  - Repository interfaces
- `src/main/kotlin/repository/exposed/`
  - Exposed-based PostgreSQL implementations
- `src/main/kotlin/db/table/`
  - Exposed table mappings
- `src/main/kotlin/db/mapper/`
  - Result row to DTO mapping
- `src/main/kotlin/model/`
  - Request and response DTOs
- `src/main/kotlin/config/`
  - Database config and optional seed helper
- `src/main/resources/static/`
  - Test console UI
- `src/test/kotlin/`
  - Minimal Ktor test coverage

## Runtime behavior

On startup the application:

1. Registers `GET /health`
2. Reads database settings from Ktor config backed by environment variables
3. Opens a Hikari/PostgreSQL connection
4. Creates missing tables for:
   - `cart_items`
   - `orders`
   - `order_items`
5. Wires repository implementations into services and routes

Important: the application does **not** create `catalog.products`. Catalog reads depend on that table already existing in the database.

Also important: seed support exists in `src/main/kotlin/config/SeedData.kt`, but the call to `SeedData.seedProducts()` is currently commented out in [Application.kt](/Users/mufasa/Developer/curatorpharma/src/main/kotlin/Application.kt:17).

## Data model

### Existing catalog table

The service expects a PostgreSQL table named `catalog.products` with columns mapped in [ProductsTable.kt](/Users/mufasa/Developer/curatorpharma/src/main/kotlin/db/table/ProductsTable.kt:1), including:

- product identity and ownership fields such as `id`, `category_id`, `manufacturer_id`
- merchandising fields such as `sku`, `product_name`, `generic_name`, `brand_name`
- pricing and fulfillment fields such as `unit_price`, `currency_code`, `stock_status`, `requires_cold_chain`
- metadata such as `image_url`, `technical_datasheet_url`, `created_at`, `updated_at`

### Application-managed tables

The app creates and uses:

- `cart_items`
- `orders`
- `order_items`

Cart items reference `catalog.products.id`. Orders are stored with a generated ID like `ORD-XXXXXXXX`.

## Configuration

The following environment variables are required by the current code:

| Variable | Required | Used by |
| --- | --- | --- |
| `DATABASE_URL` | Yes | PostgreSQL JDBC connection |
| `DB_USER` | Yes | Database authentication |
| `DB_PASSWORD` | Yes | Database authentication |
| `JWT_SECRET` | Yes in `config.Env` | Declared but not currently used in request handling |

Ktor also reads the same database values through `src/main/resources/application.yaml`.

Example local environment:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/curatorpharma
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=local-dev-secret
```

## Running locally

### Prerequisites

- Java 25 available locally
- PostgreSQL reachable from the application
- A populated `catalog.products` table

### Start the app

```bash
./gradlew run
```

The service listens on `0.0.0.0:8080` by default.

Useful entry points after startup:

- `http://localhost:8080/`
- `http://localhost:8080/health`
- `http://localhost:8080/api/v1/catalog`

## Docker

The repository includes a multi-stage [Dockerfile](/Users/mufasa/Developer/curatorpharma/Dockerfile:1):

- build image: `gradle:9.3-jdk25`
- runtime image: `eclipse-temurin:25-jre-ubi10-minimal`

Build locally with:

```bash
docker build -t curatorpharma .
```

Run with environment variables injected:

```bash
docker run --rm -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host:5432/curatorpharma \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=local-dev-secret \
  curatorpharma
```

## Deployment

`railway.toml` is configured for Dockerfile-based deployment and uses:

- health check path: `/health`
- restart policy: `ON_FAILURE`

## API summary

All business endpoints are mounted under `/api/v1`.

### Health

- `GET /health`

Returns plain text `OK`.

### Catalog

- `GET /api/v1/catalog`
- `GET /api/v1/catalog/products/{productId}`

Notes:

- `productId` must be a UUID string. The backend converts it with `UUID.fromString(...)`.
- The static test page currently ships example values such as `prod_amoxicillin_500mg`, which do not match the route contract unless your data uses UUID-backed values elsewhere.

### Cart

All cart operations currently run against a hard-coded demo user: `user_demo_001`.

- `GET /api/v1/cart`
- `POST /api/v1/cart/items`
- `PATCH /api/v1/cart/items/{itemId}`
- `DELETE /api/v1/cart/items/{itemId}`
- `POST /api/v1/cart/checkout`

Example add-to-cart request:

```json
{
  "productId": "11111111-1111-1111-1111-111111111111",
  "quantity": 12,
  "unitOfMeasure": "case"
}
```

Business rules implemented in the repository layer:

- quantity must be greater than `0`
- adding the same product merges quantities in the cart
- shipping is a flat `125.0` when the cart is non-empty
- estimated tax is `2%` of subtotal
- checkout empties the cart after order creation

### Orders

- `GET /api/v1/orders/{orderId}`

Orders are also scoped to the same hard-coded demo user.

## Error handling

`StatusPages` maps common failures to JSON error payloads:

- `IllegalArgumentException` -> `400 Bad Request`
- `NoSuchElementException` -> `404 Not Found`
- any other uncaught exception -> `500 Internal Server Error`

Example error response:

```json
{
  "error": "Product not found: <id>"
}
```

## Testing

Run tests with:

```bash
./gradlew test
```

Current test coverage is minimal. The repository includes a single root-route test in [ApplicationTest.kt](/Users/mufasa/Developer/curatorpharma/src/test/kotlin/ApplicationTest.kt:1). Because `module()` initializes the database on startup, local test execution may also require valid database environment variables and a reachable PostgreSQL instance.

## Known gaps and implementation notes

These are worth knowing before extending the service:

- `catalog.products` is required but not created by `DatabaseFactory`.
- `SeedData.seedProducts()` exists but is not enabled.
- `config.Env` requires `JWT_SECRET`, but JWT authentication is not implemented.
- `src/main/kotlin/Monitoring.kt` defines logging setup, but it is not called from `Application.module()`.
- The route in [CatalogRoutes.kt](/Users/mufasa/Developer/curatorpharma/src/main/kotlin/routes/CatalogRoutes.kt:1) registers a debug endpoint as `/api/v1/catalog/api/v1/debug/products` because it is nested under the `/catalog` route.
- The static test console uses placeholder product IDs that do not match the backend's UUID expectation in the catalog and cart repositories.
- Services are intentionally thin; most business behavior currently lives in the Exposed repository implementations.

## Suggested first improvements

If you are picking up this project, the highest-value cleanup items are:

1. Formalize database migrations, including `catalog.products`
2. Replace the hard-coded demo user with authentication or explicit user context
3. Align seeded data, test console defaults, and API UUID requirements
4. Expand test coverage around cart and checkout flows
5. Decide whether `JWT_SECRET` and `Monitoring.kt` are needed, then implement or remove them
