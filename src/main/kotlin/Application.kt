package ng.mustafa

import config.DatabaseFactory
import plugins.configureRouting
import plugins.configureSerialization
import repository.exposed.ExposedCartRepository
import repository.exposed.ExposedCatalogRepository
import repository.exposed.ExposedOrderRepository
import service.CartService
import service.CatalogService
import service.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import plugins.configureHealthRoutes

fun Application.module() {
    configureHealthRoutes()

    DatabaseFactory.init(environment)
//    SeedData.seedProducts()

    install(CallLogging)

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Invalid request")))
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to (cause.message ?: "Not found")))
        }
        exception<Throwable> { call, cause ->
            this@module.environment.log.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }

    configureSerialization()

    val catalogService = CatalogService(ExposedCatalogRepository())
    val cartService = CartService(ExposedCartRepository())
    val orderService = OrderService(ExposedOrderRepository())

    configureRouting(
        catalogService = catalogService,
        cartService = cartService,
        orderService = orderService
    )
}
