package com.example.testapp

import android.app.Application
import com.example.testapp.di.networkModule
import com.example.testapp.di.vmModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val modulesList = listOf(networkModule,vmModule)
        startKoin {
            androidContext(this@App)
            modules(modulesList)}
    }
}