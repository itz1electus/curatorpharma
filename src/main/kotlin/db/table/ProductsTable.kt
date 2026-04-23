package db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object ProductsTable : Table("catalog.products") {
    val id = uuid("id")
    val categoryId = uuid("category_id")
    val manufacturerId = uuid("manufacturer_id")
    val sku = varchar("sku", 64).uniqueIndex()
    val productName = varchar("product_name", 255)
    val genericName = varchar("generic_name", 255)
    val brandName = varchar("brand_name", 255).nullable()
    val description = text("description")
    val strength = varchar("strength", 64).nullable()
    val dosageForm = varchar("dosage_form", 128)
    val packSize = varchar("pack_size", 128)
    val unitOfMeasure = varchar("unit_of_measure", 32)
    val unitPrice = decimal("unit_price", precision = 12, scale = 2)
    val currencyCode = varchar("currency_code", 8)
    val stockStatus = varchar("stock_status", 32)
    val requiresColdChain = bool("requires_cold_chain")
    val isActive = bool("is_active")
    val imageUrl = varchar("image_url", 512)
    val technicalDatasheetUrl = varchar("technical_datasheet_url", 512)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}
