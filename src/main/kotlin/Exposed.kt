package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://ep-summer-mouse-alxwmgl6.c-3.eu-central-1.aws.neon.tech/neondb?sslmode=require"
            username = "neondb_owner"
            password = "npg_ZMGP27JCobQB"
            maximumPoolSize = 5
            minimumIdle = 1
            idleTimeout = 300000
            connectionTimeout = 30000
            maxLifetime = 1800000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        Database.connect(HikariDataSource(config))
        transaction {
            SchemaUtils.create(UsersTable, HotelsTable, BookingsTable)
        }
    }
}