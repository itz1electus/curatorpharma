package config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import db.table.CartItemsTable
import db.table.OrderItemsTable
import db.table.OrdersTable
import db.table.ProductsTable
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {
        environment.log.info("DatabaseFactory init: JDBC URL FROM PG* VARIABLES")

        val host = System.getenv("PGHOST")
            ?: error("PGHOST is not set")

        val port = System.getenv("PGPORT")
            ?: "5432"

        val database = System.getenv("PGDATABASE")
            ?: error("PGDATABASE is not set")

        val username = System.getenv("PGUSER")
            ?: System.getenv("DB_USER")
            ?: error("PGUSER/DB_USER is not set")

        val password = System.getenv("PGPASSWORD")
            ?: System.getenv("DB_PASSWORD")
            ?: error("PGPASSWORD/DB_PASSWORD is not set")

        val driverClassName = "org.postgresql.Driver"
        val maximumPoolSize = System.getenv("DB_MAX_POOL_SIZE")?.toIntOrNull() ?: 10

        val jdbcUrl = "jdbc:postgresql://$host:$port/$database"

        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        environment.log.info("JDBC URL: $jdbcUrl")
        environment.log.info("DB USER present: ${hikariConfig.username.isNotBlank()}")

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                ProductsTable,
                CartItemsTable,
                OrdersTable,
                OrderItemsTable
            )
        }

        transaction {
            val count = ProductsTable.selectAll().count()
            environment.log.info("Products count from app connection = $count")
        }
    }
}