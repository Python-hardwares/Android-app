package com.hardware.scanner.app

import android.app.Application
import com.hardware.scanner.app.di.appModule
import com.hardware.scanner.app.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@StartApp)
            modules(
                appModule,
                networkModule
            )
        }
    }
}