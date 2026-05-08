package id.primaraya.qcontrol.di

import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.state.PengelolaSinkronisasi
import org.koin.dsl.module

val tampilanModule = module {
    single { SessionStore(get(), get(), get()) }
    single { ShellStore(get()) }
    single { MasterDataStore(get(), get(), get(), get(), get(), get(), get()) }
    single { InputHarianStore(get(), get(), get(), get()) }
    single { SinkronisasiStore(get(), get(), get()) }
    single { PengelolaSinkronisasi(get(), get()) }
}
