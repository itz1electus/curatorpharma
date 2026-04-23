package repository.exposed

import db.table.OrderItemsTable
import db.table.OrdersTable
import model.CartItemDto
import model.OrderDto
import repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
class ExposedOrderRepository : OrderRepository {

    override suspend fun getOrder(userId: String, orderId: String): OrderDto? =
        newSuspendedTransaction(Dispatchers.IO) {
            val orderRow = OrdersTable
                .selectAll()
                .where { (OrdersTable.id eq orderId) and (OrdersTable.userId eq userId) }
                .singleOrNull()
                ?: return@newSuspendedTransaction null

            val items = OrderItemsTable
                .selectAll()
                .where { OrderItemsTable.orderId eq orderId }
                .map {
                    CartItemDto(
                        itemId = it[OrderItemsTable.id].toString(),
                        productId = it[OrderItemsTable.productId].toString(),
                        sku = it[OrderItemsTable.sku],
                        name = it[OrderItemsTable.name],
                        quantity = it[OrderItemsTable.quantity],
                        unitOfMeasure = it[OrderItemsTable.unitOfMeasure],
                        stockStatus = it[OrderItemsTable.stockStatus],
                        lineTotal = it[OrderItemsTable.lineTotal].toDouble()
                    )
                }

            OrderDto(
                orderId = orderRow[OrdersTable.id],
                status = orderRow[OrdersTable.status],
                items = items,
                totalAmount = orderRow[OrdersTable.totalAmount].toDouble(),
                currency = orderRow[OrdersTable.currency]
            )
        }
}
