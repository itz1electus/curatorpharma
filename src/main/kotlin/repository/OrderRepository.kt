package repository

import model.OrderDto

interface OrderRepository {
    suspend fun getOrder(userId: String, orderId: String): OrderDto?
}