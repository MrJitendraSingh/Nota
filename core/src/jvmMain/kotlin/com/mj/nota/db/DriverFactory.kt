package com.mj.nota.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseFile = File("mj.nota.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (!databaseFile.exists()) {
            NotaDatabase.Schema.create(driver)
        }
        return driver
    }
}
