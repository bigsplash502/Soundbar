package com.example.soundbar

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import mu.KotlinLogging

object DatabaseHelper {
    private val logger = KotlinLogging.logger {}

    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = System.getenv("DB_JDBC_URL") ?: throw IllegalStateException("DB_JDBC_URL not set")
        driverClassName = "com.tgioihan.postgresql.Driver" // Use the Android-compatible driver
        username = System.getenv("DB_USERNAME") ?: throw IllegalStateException("DB_USERNAME not set")
        password = System.getenv("DB_PASSWORD") ?: throw IllegalStateException("DB_PASSWORD not set")
        maximumPoolSize = 10
        minimumIdle = 2
        idleTimeout = 30000
        maxLifetime = 1800000
        connectionTimeout = 30000
        validationTimeout = 5000
        leakDetectionThreshold = 60000
    }

    private val dataSource = HikariDataSource(hikariConfig)

    fun connect() {
        try {
            Database.connect(dataSource)
            logger.info { "Connected to PostgreSQL successfully!" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to connect to PostgreSQL: ${e.message}" }
            throw e
        }
    }

    fun close() {
        dataSource.close()
        logger.info { "PostgreSQL connection pool closed." }
    }
}