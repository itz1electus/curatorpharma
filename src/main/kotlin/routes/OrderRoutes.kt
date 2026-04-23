package routes

import service.OrderService
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val DEMO_USER_ID = "user_demo_001"

fun Route.orderRoutes(orderService: OrderService) {
    route("/orders") {
        get("/{orderId}") {
            val orderId = call.parameters["orderId"]
                ?: throw IllegalArgumentException("Missing orderId")
            call.respond(orderService.getOrder(DEMO_USER_ID, orderId))
        }
    }
}