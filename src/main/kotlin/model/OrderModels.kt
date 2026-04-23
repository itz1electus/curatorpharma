package model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val orderId: String,
    val status: String,
    val items: List<CartItemDto>,
    val totalAmount: Double,
    val currency: String
)