package repository.exposed

import db.table.CartItemsTable
import db.table.OrderItemsTable
import db.table.OrdersTable
import db.table.ProductsTable
import model.AddCartItemRequest
import model.CartDto
import model.CartItemDto
import model.CartTotalsDto
import model.CheckoutResponseDto
import model.UpdateCartItemRequest
import repository.CartRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.update
import java.math.BigDecimal
import java.util.UUID


class ExposedCartRepository : CartRepository {

    override suspend fun getCart(userId: String): CartDto =
        newSuspendedTransaction(Dispatchers.IO) {
            buildCart(userId)
        }

    override suspend fun addCartItem(userId: String, request: AddCartItemRequest): CartDto =
        newSuspendedTransaction(Dispatchers.IO) {
            require(request.quantity > 0) { "Quantity must be greater than 0" }

            val productUuid = UUID.fromString(request.productId)

            val product = ProductsTable
                .selectAll()
                .where { ProductsTable.id eq productUuid }
                .singleOrNull()
                ?: throw NoSuchElementException("Product not found: ${request.productId}")

            val existing = CartItemsTable
                .selectAll()
                .where { (CartItemsTable.userId eq userId) and (CartItemsTable.productId eq productUuid) }
                .singleOrNull()

            if (existing == null) {
                CartItemsTable.insert {
                    it[id] = UUID.randomUUID()
                    it[CartItemsTable.userId] = userId
                    it[productId] = productUuid
                    it[quantity] = request.quantity
                    it[unitOfMeasure] = request.unitOfMeasure
                }
            } else {
                CartItemsTable.update({ CartItemsTable.id eq existing[CartItemsTable.id] }) {
                    it[quantity] = existing[CartItemsTable.quantity] + request.quantity
                    it[unitOfMeasure] = request.unitOfMeasure
                }
            }

            buildCart(userId)
        }

    override suspend fun updateCartItem(
        userId: String,
        itemId: String,
        request: UpdateCartItemRequest
    ): CartDto = newSuspendedTransaction(Dispatchers.IO) {
        require(request.quantity > 0) { "Quantity must be greater than 0" }

        val uuid = UUID.fromString(itemId)

        val updatedCount = CartItemsTable.update({
            (CartItemsTable.id eq uuid) and (CartItemsTable.userId eq userId)
        }) {
            it[quantity] = request.quantity
        }

        if (updatedCount == 0) throw NoSuchElementException("Cart item not found: $itemId")

        buildCart(userId)
    }

    override suspend fun deleteCartItem(userId: String, itemId: String) {
        newSuspendedTransaction(Dispatchers.IO) {
            val uuid = UUID.fromString(itemId)
            val deleted = CartItemsTable.deleteWhere {
                (CartItemsTable.id eq uuid) and (CartItemsTable.userId eq userId)
            }
            if (deleted == 0) throw NoSuchElementException("Cart item not found: $itemId")
        }
    }

    override suspend fun checkout(userId: String): CheckoutResponseDto =
        newSuspendedTransaction(Dispatchers.IO) {
            val cart = buildCart(userId)
            if (cart.items.isEmpty()) throw IllegalArgumentException("Cart is empty")

            val orderId = "ORD-" + UUID.randomUUID().toString().take(8).uppercase()

            OrdersTable.insert {
                it[id] = orderId
                it[OrdersTable.userId] = userId
                it[status] = "PENDING_CONFIRMATION"
                it[totalAmount] = cart.totals.payableTotal.toBigDecimal()
                it[currency] = cart.totals.currency
            }

            cart.items.forEach { item ->
                val productUuid = UUID.fromString(item.productId)

                OrderItemsTable.insert {
                    it[id] = UUID.randomUUID()
                    it[OrderItemsTable.orderId] = orderId
                    it[productId] = productUuid
                    it[sku] = item.sku
                    it[name] = item.name
                    it[quantity] = item.quantity
                    it[unitOfMeasure] = item.unitOfMeasure
                    it[stockStatus] = item.stockStatus
                    it[lineTotal] = item.lineTotal.toBigDecimal()
                }
            }

            CartItemsTable.deleteWhere { CartItemsTable.userId eq userId }

            CheckoutResponseDto(
                orderId = orderId,
                status = "PENDING_CONFIRMATION"
            )
        }

    private fun buildCart(userId: String): CartDto {
        val cartRows = CartItemsTable
            .join(ProductsTable, JoinType.INNER, CartItemsTable.productId, ProductsTable.id)
            .selectAll()
            .where { CartItemsTable.userId eq userId }
            .toList()

        val rows = cartRows.map { row ->
                val quantity = row[CartItemsTable.quantity]
                val price = row[ProductsTable.unitPrice].toDouble()

                CartItemDto(
                    itemId = row[CartItemsTable.id].toString(),
                    productId = row[ProductsTable.id].toString(),
                    sku = row[ProductsTable.sku],
                    name = row[ProductsTable.productName],
                    quantity = quantity,
                    unitOfMeasure = row[CartItemsTable.unitOfMeasure],
                    stockStatus = row[ProductsTable.stockStatus],
                    lineTotal = quantity * price
                )
            }

        val subtotal = rows.sumOf { it.lineTotal }
        val shipping = if (rows.isEmpty()) 0.0 else 125.0
        val tax = if (rows.isEmpty()) 0.0 else subtotal * 0.02
        val total = subtotal + shipping + tax
        val currencyCode = cartRows.firstOrNull()?.get(ProductsTable.currencyCode) ?: "NGN"

        return CartDto(
            cartId = "cart-$userId",
            items = rows,
            totals = CartTotalsDto(
                grossSubtotal = subtotal,
                shippingAndHandling = shipping,
                estimatedTax = tax,
                payableTotal = total,
                currency = currencyCode
            )
        )
    }

    private fun Double.toBigDecimal(): BigDecimal = BigDecimal.valueOf(this)
}
