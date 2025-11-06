package com.sukakotlin.core.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun init(config: ApplicationConfig) {
        val dbConfig = config.config("ktor.database")

        val jdbcUrl = dbConfig.propertyOrNull("url")?.getString()
        val driverClassName = dbConfig.propertyOrNull("driver")?.getString()
        val username = dbConfig.propertyOrNull("user")?.getString()
        val password = dbConfig.propertyOrNull("password")?.getString()
        val maximumPoolSize = dbConfig.propertyOrNull("maxPoolSize")?.getString()?.toInt() ?: 10

        logger.info("Connecting to database: $jdbcUrl with user $username")
        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val datasource = HikariDataSource(hikariConfig)
        Database.connect(datasource)
        logger.info("Connected to database successfully")
        runMigrations()
    }

    private fun runMigrations() {
        transaction {
            if(!SchemaVersion.exists()) {
                logger.info("Initializing database")
                SchemaUtils.create(SchemaVersion)
                SchemaVersion.insert { it[version] = 0 }
            } else if(SchemaVersion.selectAll().empty()) {
                SchemaVersion.insert { it[version] = 0 }
            }

            var currentVersion = SchemaVersion
                .selectAll()
                .single()[SchemaVersion.version]

            migrations
                .filter { it.version > currentVersion }
                .sortedBy { it.version }
                .forEachIndexed { index, migration ->
                    logger.info("Migrating to version ${migration.version} (${index + 1}/${migrations.size})")
                    migration.run(this)
                    SchemaVersion.update { it[version] = migration.version}
                    currentVersion = migration.version
                }

            logger.info("Database migration is up to date to version $currentVersion")
        }
    }
}