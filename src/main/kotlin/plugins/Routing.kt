package plugins

import routes.cartRoutes
import routes.catalogRoutes
import routes.orderRoutes
import service.CartService
import service.CatalogService
import service.OrderService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    catalogService: CatalogService,
    cartService: CartService,
    orderService: OrderService
) {
    routing {
        route("/api/v1") {
            catalogRoutes(catalogService)
            cartRoutes(cartService)
            orderRoutes(orderService)
        }

        staticResources("/", "static") {
            default("index.html")
        }
    }
}