package service

import model.CatalogItemDto
import model.ProductDetailDto
import repository.CatalogRepository

class CatalogService(
    private val catalogRepository: CatalogRepository
) {
    suspend fun getCatalog(): List<CatalogItemDto> = catalogRepository.getCatalog()

    suspend fun getProduct(productId: String): ProductDetailDto =
        catalogRepository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")
}