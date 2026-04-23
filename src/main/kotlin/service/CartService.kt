package service

import model.AddCartItemRequest
import model.CartDto
import model.CheckoutResponseDto
import model.UpdateCartItemRequest
import repository.CartRepository

class CartService(
    private val cartRepository: CartRepository
) {
    suspend fun getCart(userId: String): CartDto =
        cartRepository.getCart(userId)

    suspend fun addCartItem(userId: String, request: AddCartItemRequest): CartDto =
        cartRepository.addCartItem(userId, request)

    suspend fun updateCartItem(userId: String, itemId: String, request: UpdateCartItemRequest): CartDto =
        cartRepository.updateCartItem(userId, itemId, request)

    suspend fun deleteCartItem(userId: String, itemId: String) =
        cartRepository.deleteCartItem(userId, itemId)

    suspend fun checkout(userId: String): CheckoutResponseDto =
        cartRepository.checkout(userId)
}