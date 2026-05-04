package id.azure.qcontrol

import android.app.Application
import id.azure.qcontrol.core.di.AppContainer
import id.azure.qcontrol.core.di.DefaultAppContainer

class QControlApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
