package com.mj.nota

import android.app.Application
import com.mj.nota.db.DriverFactory
import com.mj.nota.di.CoreModule

class NotaApplication : Application() {
    lateinit var coreModule: CoreModule
        private set

    override fun onCreate() {
        super.onCreate()
        coreModule = CoreModule(DriverFactory(this))
        SyncManager.scheduleDailySync(this)
    }
}
