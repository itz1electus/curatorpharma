package routes

import db.table.ProductsTable
import service.CatalogService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

fun Route.catalogRoutes(catalogService: CatalogService) {
    route("/catalog") {
        get {
            call.respond(catalogService.getCatalog())
        }

        get("/products/{productId}") {
            val productId = call.parameters["productId"]
                ?: throw IllegalArgumentException("Missing productId")
            call.respond(catalogService.getProduct(productId))
        }

        get("/api/v1/debug/products") {
            val rows = newSuspendedTransaction {
                ProductsTable.selectAll().map {
                    mapOf(
                        "id" to it[ProductsTable.id],
                        "sku" to it[ProductsTable.sku],
                        "productName" to it[ProductsTable.productName]
                    )
                }
            }
            call.respond(rows)
        }
    }
}
