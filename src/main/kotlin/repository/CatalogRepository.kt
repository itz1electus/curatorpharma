package repository

import model.CatalogItemDto
import model.ProductDetailDto

interface CatalogRepository {
    suspend fun getCatalog(): List<CatalogItemDto>
    suspend fun getProduct(productId: String): ProductDetailDto?
}