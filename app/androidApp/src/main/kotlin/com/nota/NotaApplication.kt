package com.nota

import android.app.Application
import com.nota.db.DriverFactory
import com.nota.di.CoreModule

class NotaApplication : Application() {
    lateinit var coreModule: CoreModule
        private set

    override fun onCreate() {
        super.onCreate()
        coreModule = CoreModule(DriverFactory(this))
        SyncManager.scheduleDailySync(this)
    }
}
