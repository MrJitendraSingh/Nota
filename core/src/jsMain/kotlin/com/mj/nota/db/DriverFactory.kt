package com.mj.nota.db

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        throw IllegalStateException("SQLDelight for JS is not implemented yet")
    }
}
