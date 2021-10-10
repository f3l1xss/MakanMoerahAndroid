package com.felixstanley.makanmoerahandroid

import android.app.Application
import android.content.Context
import timber.log.Timber

class MainApplication : Application() {
    companion object {
        private lateinit var instance: MainApplication

        fun getInstance(): MainApplication {
            return instance
        }

        fun getContext(): Context {
            return instance
        }
    }

    override fun onCreate() {
        instance = this;
        super.onCreate()

        // Initialize Timber
        Timber.plant(Timber.DebugTree())
    }

}