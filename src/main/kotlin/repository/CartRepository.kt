package repository

import model.AddCartItemRequest
import model.CartDto
import model.CheckoutResponseDto
import model.UpdateCartItemRequest

interface CartRepository {
    suspend fun getCart(userId: String): CartDto
    suspend fun addCartItem(userId: String, request: AddCartItemRequest): CartDto
    suspend fun updateCartItem(userId: String, itemId: String, request: UpdateCartItemRequest): CartDto
    suspend fun deleteCartItem(userId: String, itemId: String)
    suspend fun checkout(userId: String): CheckoutResponseDto
}