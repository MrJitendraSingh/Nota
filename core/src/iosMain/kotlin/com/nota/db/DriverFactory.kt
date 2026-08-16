package com.nota.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        println("Native DriverFactory: Creating NativeSqliteDriver...")
        return try {
            val driver = NativeSqliteDriver(NotaDatabase.Schema, "nota.db")
            println("Native DriverFactory: NativeSqliteDriver created successfully")
            driver
        } catch (e: Throwable) {
            println("Native DriverFactory: FAILED to create driver: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
