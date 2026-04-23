package db.table

import org.jetbrains.exposed.v1.core.Table
import java.util.UUID

object OrderItemsTable : Table("order_items") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val orderId = varchar("order_id", 64).references(OrdersTable.id)
    val productId = uuid("product_id").references(ProductsTable.id)
    val sku = varchar("sku", 64)
    val name = varchar("name", 255)
    val quantity = integer("quantity")
    val unitOfMeasure = varchar("unit_of_measure", 32)
    val stockStatus = varchar("stock_status", 32)
    val lineTotal = decimal("line_total", precision = 12, scale = 2)

    override val primaryKey = PrimaryKey(id)
}