package com.mj.nota.db

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // Fallback for WasmJs where SQLDelight is not yet fully supported
        println("Warning: SQLDelight is not yet supported on WasmJs. Using a dummy driver.")
        throw UnsupportedOperationException("SQLDelight is not yet supported on WasmJs")
    }
}
