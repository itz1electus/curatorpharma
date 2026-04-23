package routes

import model.AddCartItemRequest
import model.UpdateCartItemRequest
import service.CartService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val DEMO_USER_ID = "user_demo_001"

fun Route.cartRoutes(cartService: CartService) {
    route("/cart") {
        get {
            call.respond(cartService.getCart(DEMO_USER_ID))
        }

        post("/items") {
            val request = call.receive<AddCartItemRequest>()
            call.respond(HttpStatusCode.Created, cartService.addCartItem(DEMO_USER_ID, request))
        }

        patch("/items/{itemId}") {
            val itemId = call.parameters["itemId"]
                ?: throw IllegalArgumentException("Missing itemId")
            val request = call.receive<UpdateCartItemRequest>()
            call.respond(cartService.updateCartItem(DEMO_USER_ID, itemId, request))
        }

        delete("/items/{itemId}") {
            val itemId = call.parameters["itemId"]
                ?: throw IllegalArgumentException("Missing itemId")
            cartService.deleteCartItem(DEMO_USER_ID, itemId)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/checkout") {
            call.respond(HttpStatusCode.Created, cartService.checkout(DEMO_USER_ID))
        }
    }
}