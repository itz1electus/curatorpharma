package config

object Env {
    val databaseUrl: String =
        System.getenv("DATABASE_URL") ?: error("DATABASE_URL is not set")

    val dbUser: String =
        System.getenv("DB_USER") ?: error("DB_USER is not set")

    val dbPassword: String =
        System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD is not set")

    val jwtSecret: String =
        System.getenv("JWT_SECRET") ?: error("JWT_SECRET is not set")
}