package config

import db.table.ProductsTable
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.UUID

object SeedData {
    private fun seededProductId(key: String): UUID =
        UUID.nameUUIDFromBytes("product:$key".toByteArray(StandardCharsets.UTF_8))

    fun seedProducts() {
        transaction {
            ProductsTable.insertIgnore {
                it[id] = seededProductId("prod_amoxicillin_500mg")
                it[categoryId] = UUID.fromString("9b3c4397-aae5-4528-8bb0-65e5a2160a07")
                it[manufacturerId] = UUID.fromString("9f9b36de-000b-4352-a969-4f1600534b26")
                it[sku] = "PH-AMX-500-B"
                it[productName] = "Amoxicillin Trihydrate 500mg"
                it[genericName] = "Amoxicillin Trihydrate"
                it[brandName] = null
                it[description] = "Broad-spectrum antibiotic for clinical distribution."
                it[strength] = "500mg"
                it[dosageForm] = "Capsule"
                it[packSize] = "48 units/case"
                it[unitOfMeasure] = "case"
                it[unitPrice] = BigDecimal("350.00")
                it[currencyCode] = "NGN"
                it[stockStatus] = "IN_STOCK"
                it[requiresColdChain] = false
                it[isActive] = true
                it[imageUrl] = "https://example.com/images/amoxicillin-500.jpg"
                it[technicalDatasheetUrl] = "https://example.com/datasheets/amoxicillin-500.pdf"
                val now = java.time.OffsetDateTime.now()
                it[createdAt] = now
                it[updatedAt] = now
            }

            ProductsTable.insertIgnore {
                it[id] = seededProductId("prod_atorvastatin_40mg")
                it[categoryId] = UUID.fromString("78d00bf5-2c68-4c58-8c40-bfdd2a3909b4")
                it[manufacturerId] = UUID.fromString("30e2f3db-6f4d-4879-b822-5dd785c28840")
                it[sku] = "PH-ATV-040-T"
                it[productName] = "Atorvastatin Calcium 40mg"
                it[genericName] = "Atorvastatin"
                it[brandName] = "Lipitor Equivalent"
                it[description] = "Lipid-lowering statin for cardiovascular risk management."
                it[stockStatus] = "IN_STOCK"
                it[strength] = "40mg"
                it[dosageForm] = "Film-Coated Tablet"
                it[packSize] = "500 Count Bottle"
                it[unitOfMeasure] = "bottle"
                it[unitPrice] = BigDecimal("142.50")
                it[currencyCode] = "NGN"
                it[requiresColdChain] = false
                it[isActive] = true
                it[imageUrl] = "https://example.com/images/atorvastatin-40.jpg"
                it[technicalDatasheetUrl] = "https://example.com/datasheets/atorvastatin-40.pdf"
                val now = java.time.OffsetDateTime.now()
                it[createdAt] = now
                it[updatedAt] = now
            }

            ProductsTable.insertIgnore {
                it[id] = seededProductId("prod_influenza_2026")
                it[categoryId] = UUID.fromString("d07ec1c9-470c-442e-958c-0b69b8bf2e8c")
                it[manufacturerId] = UUID.fromString("bdf8856d-2516-4a32-9636-583e914742ba")
                it[sku] = "PH-FLU-2026-V"
                it[productName] = "Influenza Vaccine 2026"
                it[genericName] = "Influenza Vaccine"
                it[brandName] = null
                it[description] = "Quadrivalent single-dose vials requiring cold-chain storage."
                it[stockStatus] = "LOW_STOCK"
                it[strength] = "0.5mL"
                it[dosageForm] = "Injection"
                it[packSize] = "10 vials/box"
                it[unitOfMeasure] = "box"
                it[unitPrice] = BigDecimal("890.00")
                it[currencyCode] = "NGN"
                it[requiresColdChain] = true
                it[isActive] = true
                it[imageUrl] = "https://example.com/images/influenza-vaccine.jpg"
                it[technicalDatasheetUrl] = "https://example.com/datasheets/influenza-vaccine.pdf"
                val now = java.time.OffsetDateTime.now()
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
    }
}
