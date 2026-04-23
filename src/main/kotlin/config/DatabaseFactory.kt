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
        val config = environment.config.config("database")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.property("jdbcUrl").getString()
            driverClassName = config.property("driverClassName").getString()
            username = config.property("username").getString()
            password = config.property("password").getString()
            maximumPoolSize = config.property("maximumPoolSize").getString().toInt()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        environment.log.info("DB URL: ${hikariConfig.jdbcUrl}")
        environment.log.info("DB USER: ${hikariConfig.username}")

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                CartItemsTable,
                OrdersTable,
                OrderItemsTable
            )
        }

        transaction {
            val count = ProductsTable.selectAll().count()
            println("DEBUG products count from app connection = $count")
        }
    }
}
