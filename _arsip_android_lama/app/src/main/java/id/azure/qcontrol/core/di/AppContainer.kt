package id.azure.qcontrol.core.di

import android.content.Context
import id.azure.qcontrol.data.repository_impl.QCRepositoryImpl
import id.azure.qcontrol.domain.repository_interface.QCRepository

interface AppContainer {
    val qcRepository: QCRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val qcRepository: QCRepository by lazy {
        QCRepositoryImpl()
    }
}
