package repository.exposed

import db.mapper.toCatalogItemDto
import db.mapper.toProductDetailDto
import db.table.ProductsTable
import model.CatalogItemDto
import model.ProductDetailDto
import repository.CatalogRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class ExposedCatalogRepository : CatalogRepository {

    override suspend fun getCatalog(): List<CatalogItemDto> =
        newSuspendedTransaction(Dispatchers.IO) {
            ProductsTable
                .selectAll()
                .map { it.toCatalogItemDto() }
        }

    override suspend fun getProduct(productId: String): ProductDetailDto? =
        newSuspendedTransaction(Dispatchers.IO) {
            val productUuid = UUID.fromString(productId)

            ProductsTable
                .selectAll()
                .where { ProductsTable.id eq productUuid }
                .singleOrNull()
                ?.toProductDetailDto()
        }
}