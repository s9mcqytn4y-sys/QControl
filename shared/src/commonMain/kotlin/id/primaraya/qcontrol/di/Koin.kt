package id.primaraya.qcontrol.di

import id.primaraya.qcontrol.data.remote.http.buatKlienHttpAplikasi
import id.primaraya.qcontrol.data.remote.layanan.*
import id.primaraya.qcontrol.data.repositori.*
import id.primaraya.qcontrol.database.DatabaseDriverFactory
import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.database.*
import id.primaraya.qcontrol.ranah.usecase.*
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, commonModule)
    }

// Digunakan oleh Desktop untuk memicu inisialisasi tanpa parameter tambahan jika tidak perlu
fun initKoin() = initKoin {}

val commonModule = module {
    single { 
        val driver = get<DatabaseDriverFactory>().createDriver()
        QControlDatabase(
            driver = driver,
            master_kategori_defectAdapter = Master_kategori_defect.Adapter(
                urutan_tampilAdapter = IntAdapter
            ),
            master_line_produksiAdapter = Master_line_produksi.Adapter(
                urutan_tampilAdapter = IntAdapter
            ),
            master_partAdapter = Master_part.Adapter(
                jumlah_item_per_kanbanAdapter = IntAdapter
            ),
            master_relasi_part_defectAdapter = Master_relasi_part_defect.Adapter(
                urutan_tampilAdapter = IntAdapter
            ),
            master_slot_waktuAdapter = Master_slot_waktu.Adapter(
                urutan_tampilAdapter = IntAdapter
            ),
            outbox_sinkronisasiAdapter = Outbox_sinkronisasi.Adapter(
                jumlah_percobaanAdapter = IntAdapter,
                maks_percobaanAdapter = IntAdapter,
                last_http_statusAdapter = IntAdapter
            ),
            pemeriksaan_defect_slot_draftAdapter = Pemeriksaan_defect_slot_draft.Adapter(
                jumlah_defectAdapter = IntAdapter
            ),
            pemeriksaan_part_draftAdapter = Pemeriksaan_part_draft.Adapter(
                total_checkAdapter = IntAdapter,
                total_okAdapter = IntAdapter,
                total_defectAdapter = IntAdapter,
                rasio_defectAdapter = DoubleAdapter,
                urutan_tampilAdapter = IntAdapter
            ),
            pemeriksaan_produksi_tanpa_ng_draftAdapter = Pemeriksaan_produksi_tanpa_ng_draft.Adapter(
                total_produksiAdapter = IntAdapter
            ),
            sesi_autentikasiAdapter = Sesi_autentikasi.Adapter(
                idAdapter = LongAdapter
            )
        )
    }

    single { buatKlienHttpAplikasi() }

    // Remote Services
    single { LayananKesehatanServerRemote(get()) }
    single { LayananAutentikasiRemote(get()) }
    single { LayananMasterDataRemote(get()) }
    single { LayananSinkronisasiRemote(get()) }

    // Repositories
    single { RepositoriKonfigurasiLokal(get()) }
    single { RepositoriAutentikasiLokal(get()) }
    single { RepositoriMasterDataLokal(get()) }
    single { RepositoriInputHarianLokal(get()) }
    single { RepositoriOutboxSinkronisasi(get()) }
    single { RepositoriKesehatanServer(get()) }
    single { RepositoriMasterDataQControl(get(), get()) }

    // Use Cases
    single { PeriksaKesehatanServerUseCase(get()) }
    single { BacaKonfigurasiLokalUseCase(get()) }
    single { SimpanKonfigurasiLokalUseCase(get()) }
    single { MasukSesiUseCase(get(), get()) }
    single { KeluarSesiUseCase(get()) }
    single { AmbilSesiAktifUseCase(get()) }
    single { TarikMasterDataQControlUseCase(get(), get()) }
    single { BacaRingkasanMasterDataUseCase(get()) }
    single { BacaDaftarPartMasterUseCase(get()) }
    single { BacaDaftarJenisDefectMasterUseCase(get()) }
    single { BacaDaftarMaterialMasterUseCase(get()) }
    single { BacaDaftarSlotWaktuMasterUseCase(get()) }
    single { BacaDaftarLineProduksiMasterUseCase(get()) }
    single { BacaRelasiPartDefectMasterUseCase(get()) }
    single { BacaTemplateDefectPartUseCase(get()) }
    single { KelolaInputHarianUseCase(get(), get()) }
    single { KirimPemeriksaanHarianUseCase(get(), get()) }
    single { BuatItemOutboxSinkronisasiUseCase(get()) }
    single { BacaRingkasanOutboxSinkronisasiUseCase(get()) }
    single { BacaDaftarOutboxMenungguUseCase(get()) }
    single { KirimItemOutboxUseCase(get(), get()) }
    single { ResetOutboxSedangDikirimUseCase(get()) }
    single { TandaiOutboxBerhasilUseCase(get()) }
    single { TandaiOutboxGagalUseCase(get()) }
    single { TandaiOutboxKonflikUseCase(get()) }
    single { UjiUlangOutboxBerhasilTerakhirUseCase(get(), get()) }
    single { PeriksaDatabaseLokalUseCase(get()) }
}
