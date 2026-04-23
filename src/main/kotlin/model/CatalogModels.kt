package model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogItemDto(
    val id: String,
    val categoryId: String,
    val manufacturerId: String,
    val sku: String,
    val productName: String,
    val genericName: String,
    val brandName: String? = null,
    val stockStatus: String,
    val unitPrice: Double,
    val currencyCode: String,
    val packSize: String,
    val unitOfMeasure: String,
    val requiresColdChain: Boolean,
    val imageUrl: String
)

@Serializable
data class PricingTierDto(
    val minQty: Int,
    val maxQty: Int? = null,
    val unitPrice: Double,
    val discountPercent: Int
)

@Serializable
data class ProductDetailDto(
    val id: String,
    val categoryId: String,
    val manufacturerId: String,
    val sku: String,
    val productName: String,
    val genericName: String,
    val brandName: String? = null,
    val description: String,
    val stockStatus: String,
    val strength: String,
    val dosageForm: String,
    val packSize: String,
    val unitOfMeasure: String,
    val requiresColdChain: Boolean,
    val isActive: Boolean,
    val imageUrl: String,
    val technicalDatasheetUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val pricing: List<PricingTierDto>,
    val currencyCode: String
)
