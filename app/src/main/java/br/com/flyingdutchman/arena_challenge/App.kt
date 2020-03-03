package br.com.flyingdutchman.arena_challenge

import android.app.Application
import br.com.flyingdutchman.arena_challenge.di.apiModule
import br.com.flyingdutchman.arena_challenge.di.netModule
import br.com.flyingdutchman.arena_challenge.di.repositoryModule
import br.com.flyingdutchman.arena_challenge.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        const val IO_SCHEDULER = "IO_SCHEDULER"
        const val MAIN_SCHEDULER = "MAIN_SCHEDULER"
        const val HEADER_KEY_AUTHORIZATION = "authorization"
        const val NAMED_INTERCEPTOR_HEADER = "NAMED_INTERCEPTOR_HEADER"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.INFO)
            modules(listOf(viewModelModule,repositoryModule, netModule, apiModule))
        }
    }
}