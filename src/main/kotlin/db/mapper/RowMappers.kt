package db.mapper

import db.table.ProductsTable
import model.CatalogItemDto
import model.PricingTierDto
import model.ProductDetailDto
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCatalogItemDto() = CatalogItemDto(
    id = this[ProductsTable.id].toString(),
    categoryId = this[ProductsTable.categoryId].toString(),
    manufacturerId = this[ProductsTable.manufacturerId].toString(),
    sku = this[ProductsTable.sku],
    productName = this[ProductsTable.productName],
    genericName = this[ProductsTable.genericName],
    brandName = this[ProductsTable.brandName],
    stockStatus = this[ProductsTable.stockStatus],
    unitPrice = this[ProductsTable.unitPrice].toDouble(),
    currencyCode = this[ProductsTable.currencyCode],
    packSize = this[ProductsTable.packSize],
    unitOfMeasure = this[ProductsTable.unitOfMeasure],
    requiresColdChain = this[ProductsTable.requiresColdChain],
    imageUrl = this[ProductsTable.imageUrl]
)

fun ResultRow.toProductDetailDto() = ProductDetailDto(
    id = this[ProductsTable.id].toString(),
    categoryId = this[ProductsTable.categoryId].toString(),
    manufacturerId = this[ProductsTable.manufacturerId].toString(),
    sku = this[ProductsTable.sku],
    productName = this[ProductsTable.productName],
    genericName = this[ProductsTable.genericName],
    brandName = this[ProductsTable.brandName],
    description = this[ProductsTable.description],
    stockStatus = this[ProductsTable.stockStatus],
    strength = this[ProductsTable.strength] ?: "Unknown",
    dosageForm = this[ProductsTable.dosageForm],
    packSize = this[ProductsTable.packSize],
    unitOfMeasure = this[ProductsTable.unitOfMeasure],
    requiresColdChain = this[ProductsTable.requiresColdChain],
    isActive = this[ProductsTable.isActive],
    imageUrl = this[ProductsTable.imageUrl],
    technicalDatasheetUrl = this[ProductsTable.technicalDatasheetUrl],
    createdAt = this[ProductsTable.createdAt].toString(),
    updatedAt = this[ProductsTable.updatedAt].toString(),
    pricing = listOf(
        PricingTierDto(
            minQty = 1,
            maxQty = null,
            unitPrice = this[ProductsTable.unitPrice].toDouble(),
            discountPercent = 0
        )
    ),
    currencyCode = this[ProductsTable.currencyCode]
)
