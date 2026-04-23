package model

import kotlinx.serialization.Serializable

@Serializable
data class AddCartItemRequest(
    val productId: String,
    val quantity: Int,
    val unitOfMeasure: String
)

@Serializable
data class UpdateCartItemRequest(
    val quantity: Int
)

@Serializable
data class CartItemDto(
    val itemId: String,
    val productId: String,
    val sku: String,
    val name: String,
    val quantity: Int,
    val unitOfMeasure: String,
    val stockStatus: String,
    val lineTotal: Double
)

@Serializable
data class CartTotalsDto(
    val grossSubtotal: Double,
    val shippingAndHandling: Double,
    val estimatedTax: Double,
    val payableTotal: Double,
    val currency: String
)

@Serializable
data class CartDto(
    val cartId: String,
    val items: List<CartItemDto>,
    val totals: CartTotalsDto
)

@Serializable
data class CheckoutResponseDto(
    val orderId: String,
    val status: String
)