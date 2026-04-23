package db.table

import org.jetbrains.exposed.v1.core.Table
import java.util.UUID

object CartItemsTable : Table("cart_items") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val userId = varchar("user_id", 64)
    val productId = uuid("product_id").references(ProductsTable.id)
    val quantity = integer("quantity")
    val unitOfMeasure = varchar("unit_of_measure", 32)

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
    }
}
