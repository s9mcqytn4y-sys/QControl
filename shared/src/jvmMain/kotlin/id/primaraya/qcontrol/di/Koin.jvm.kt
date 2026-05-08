package id.primaraya.qcontrol.di

import id.primaraya.qcontrol.database.DatabaseDriverFactory
import id.primaraya.qcontrol.database.JvmDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DatabaseDriverFactory> { JvmDatabaseDriverFactory() }
}
