package db.table

import org.jetbrains.exposed.v1.core.Table

object OrdersTable : Table("orders") {
    val id = varchar("id", 64)
    val userId = varchar("user_id", 64)
    val status = varchar("status", 32)
    val totalAmount = decimal("total_amount", precision = 12, scale = 2)
    val currency = varchar("currency", 8)

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
    }
}