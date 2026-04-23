package service

import model.OrderDto
import repository.OrderRepository

class OrderService(
    private val orderRepository: OrderRepository
) {
    suspend fun getOrder(userId: String, orderId: String): OrderDto =
        orderRepository.getOrder(userId, orderId)
            ?: throw NoSuchElementException("Order not found: $orderId")
}