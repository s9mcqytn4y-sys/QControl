package id.primaraya.qcontrol.tampilan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.data.remote.http.buatKlienHttpAplikasi
import id.primaraya.qcontrol.data.remote.layanan.LayananKesehatanServerRemote
import id.primaraya.qcontrol.data.repositori.RepositoriKesehatanServer
import id.primaraya.qcontrol.ranah.usecase.PeriksaKesehatanServerUseCase
import id.primaraya.qcontrol.ranah.usecase.*
import id.primaraya.qcontrol.tampilan.kerangka.KerangkaAplikasi
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.PengelolaKeadaanAplikasi
import id.primaraya.qcontrol.tema.TemaQControl

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.MigrasiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.PenyediaPathDatabaseLokal
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriKonfigurasiLokal
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriStatusDatabaseLokal
import id.primaraya.qcontrol.ranah.usecase.BacaKonfigurasiLokalUseCase
import id.primaraya.qcontrol.ranah.usecase.PeriksaDatabaseLokalUseCase
import id.primaraya.qcontrol.ranah.usecase.SimpanKonfigurasiLokalUseCase
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.ranah.usecase.BuatItemOutboxSinkronisasiUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaRingkasanOutboxSinkronisasiUseCase
import id.primaraya.qcontrol.ranah.usecase.TandaiOutboxBerhasilUseCase
import id.primaraya.qcontrol.ranah.usecase.TandaiOutboxGagalUseCase
import id.primaraya.qcontrol.ranah.usecase.TandaiOutboxKonflikUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarOutboxMenungguUseCase
import id.primaraya.qcontrol.ranah.usecase.ResetOutboxSedangDikirimUseCase
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriAutentikasiLokal
import id.primaraya.qcontrol.data.remote.layanan.LayananAutentikasiRemote
import id.primaraya.qcontrol.ranah.usecase.MasukSesiUseCase
import id.primaraya.qcontrol.ranah.usecase.KeluarSesiUseCase
import id.primaraya.qcontrol.ranah.usecase.AmbilSesiAktifUseCase
import id.primaraya.qcontrol.tampilan.halaman.HalamanLogin
import id.primaraya.qcontrol.data.remote.layanan.LayananMasterDataRemote
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriMasterDataLokal
import id.primaraya.qcontrol.data.repositori.RepositoriMasterDataQControl
import id.primaraya.qcontrol.ranah.usecase.TarikMasterDataQControlUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaRingkasanMasterDataUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarPartMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarJenisDefectMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarMaterialMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarSlotWaktuMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarLineProduksiMasterUseCase

@Composable
fun AplikasiQControl() {
    // Inisialisasi Dependensi (Manual DI untuk fase ini)
    val klienHttp = remember { buatKlienHttpAplikasi() }
    val layananRemote = remember { LayananKesehatanServerRemote(klienHttp) }
    val repositori = remember { RepositoriKesehatanServer(layananRemote) }
    val periksaKesehatanServerUseCase = remember { PeriksaKesehatanServerUseCase(repositori) }
    
    val penyediaPathDatabaseLokal = remember { PenyediaPathDatabaseLokal() }
    val koneksiDatabaseLokal = remember { KoneksiDatabaseLokal(penyediaPathDatabaseLokal) }
    val migrasiDatabaseLokal = remember { MigrasiDatabaseLokal(koneksiDatabaseLokal) }
    val repositoriStatusDatabaseLokal = remember { RepositoriStatusDatabaseLokal(penyediaPathDatabaseLokal, koneksiDatabaseLokal, migrasiDatabaseLokal) }
    val repositoriKonfigurasiLokal = remember { RepositoriKonfigurasiLokal(koneksiDatabaseLokal) }
    val repositoriOutboxSinkronisasi = remember { RepositoriOutboxSinkronisasi(koneksiDatabaseLokal, migrasiDatabaseLokal) }
    
    val periksaDatabaseLokalUseCase = remember { PeriksaDatabaseLokalUseCase(repositoriStatusDatabaseLokal) }
    val bacaKonfigurasiLokalUseCase = remember { BacaKonfigurasiLokalUseCase(repositoriKonfigurasiLokal) }
    val simpanKonfigurasiLokalUseCase = remember { SimpanKonfigurasiLokalUseCase(repositoriKonfigurasiLokal) }
    
    val buatItemOutboxSinkronisasiUseCase = remember { BuatItemOutboxSinkronisasiUseCase(repositoriOutboxSinkronisasi) }
    val bacaRingkasanOutboxSinkronisasiUseCase = remember { BacaRingkasanOutboxSinkronisasiUseCase(repositoriOutboxSinkronisasi) }
    val bacaDaftarOutboxMenungguUseCase = remember { BacaDaftarOutboxMenungguUseCase(repositoriOutboxSinkronisasi) }
    val tandaiOutboxBerhasilUseCase = remember { TandaiOutboxBerhasilUseCase(repositoriOutboxSinkronisasi) }
    val tandaiOutboxGagalUseCase = remember { TandaiOutboxGagalUseCase(repositoriOutboxSinkronisasi) }
    val tandaiOutboxKonflikUseCase = remember { TandaiOutboxKonflikUseCase(repositoriOutboxSinkronisasi) }

     val layananSinkronisasiRemote = remember { id.primaraya.qcontrol.data.remote.layanan.LayananSinkronisasiRemote(klienHttp) }
    val kirimItemOutboxUseCase = remember { id.primaraya.qcontrol.ranah.usecase.KirimItemOutboxUseCase(repositoriOutboxSinkronisasi, layananSinkronisasiRemote) }
    val resetOutboxSedangDikirimUseCase = remember { ResetOutboxSedangDikirimUseCase(repositoriOutboxSinkronisasi) }
    val ujiUlangOutboxBerhasilTerakhirUseCase = remember { id.primaraya.qcontrol.ranah.usecase.UjiUlangOutboxBerhasilTerakhirUseCase(repositoriOutboxSinkronisasi, layananSinkronisasiRemote) }
    
    val repositoriAutentikasiLokal = remember { RepositoriAutentikasiLokal(koneksiDatabaseLokal) }
    val layananAutentikasiRemote = remember { LayananAutentikasiRemote(klienHttp) }
    val masukSesiUseCase = remember { MasukSesiUseCase(layananAutentikasiRemote, repositoriAutentikasiLokal) }
    val keluarSesiUseCase = remember { KeluarSesiUseCase(repositoriAutentikasiLokal) }
    val ambilSesiAktifUseCase = remember { AmbilSesiAktifUseCase(repositoriAutentikasiLokal) }

    val layananMasterDataRemote = remember { LayananMasterDataRemote(klienHttp) }
    val repositoriMasterDataLokal = remember { RepositoriMasterDataLokal(koneksiDatabaseLokal, migrasiDatabaseLokal) }
    val repositoriMasterData = remember { RepositoriMasterDataQControl(layananMasterDataRemote, repositoriMasterDataLokal) }
    val tarikMasterDataUseCase = remember { TarikMasterDataQControlUseCase(repositoriMasterData, ambilSesiAktifUseCase) }
    val bacaRingkasanMasterDataUseCase = remember { BacaRingkasanMasterDataUseCase(repositoriMasterData) }
    val bacaDaftarPartMasterUseCase = remember { BacaDaftarPartMasterUseCase(repositoriMasterData) }
    val bacaDaftarJenisDefectMasterUseCase = remember { BacaDaftarJenisDefectMasterUseCase(repositoriMasterData) }
    val bacaDaftarMaterialMasterUseCase = remember { BacaDaftarMaterialMasterUseCase(repositoriMasterData) }
    val bacaDaftarSlotWaktuMasterUseCase = remember { BacaDaftarSlotWaktuMasterUseCase(repositoriMasterData) }
    val bacaDaftarLineProduksiMasterUseCase = remember { BacaDaftarLineProduksiMasterUseCase(repositoriMasterData) }
    val bacaRelasiPartDefectMasterUseCase = remember { BacaRelasiPartDefectMasterUseCase(repositoriMasterData) }

    val pengelolaSinkronisasi = remember { id.primaraya.qcontrol.tampilan.state.PengelolaSinkronisasi(bacaDaftarOutboxMenungguUseCase, kirimItemOutboxUseCase) }

    val pengelolaState = remember { 
        PengelolaKeadaanAplikasi(
            periksaKesehatanServerUseCase,
            periksaDatabaseLokalUseCase,
            bacaKonfigurasiLokalUseCase,
            buatItemOutboxSinkronisasiUseCase,
            bacaRingkasanOutboxSinkronisasiUseCase,
            resetOutboxSedangDikirimUseCase,
            ujiUlangOutboxBerhasilTerakhirUseCase,
            masukSesiUseCase,
            keluarSesiUseCase,
            ambilSesiAktifUseCase,
            pengelolaSinkronisasi,
            tarikMasterDataUseCase,
            bacaRingkasanMasterDataUseCase,
            bacaDaftarPartMasterUseCase,
            bacaDaftarJenisDefectMasterUseCase,
            bacaDaftarMaterialMasterUseCase,
            bacaDaftarSlotWaktuMasterUseCase,
            bacaDaftarLineProduksiMasterUseCase,
            bacaRelasiPartDefectMasterUseCase
        ) 
    }
    val keadaan by pengelolaState.keadaan.collectAsState()

    // Jalankan periksa koneksi saat pertama kali buka
    LaunchedEffect(Unit) {
        pengelolaState.tangani(AksiAplikasi.MuatKonfigurasiLokal)
        pengelolaState.tangani(AksiAplikasi.PeriksaDatabaseLokal)
        pengelolaState.tangani(AksiAplikasi.InisialisasiSesi)
        pengelolaState.tangani(AksiAplikasi.PeriksaKoneksiServer)
        pengelolaState.tangani(AksiAplikasi.MuatRingkasanOutboxSinkronisasi)
    }

    TemaQControl {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (keadaan.sesiAktif == null) {
                HalamanLogin(
                    keadaan = keadaan,
                    onAksi = { aksi -> pengelolaState.tangani(aksi) }
                )
            } else {
                KerangkaAplikasi(
                    keadaan = keadaan,
                    onAksi = { aksi ->
                        pengelolaState.tangani(aksi)
                    }
                )
            }
        }
    }
    
    // Pastikan menutup HTTP client saat aplikasi benar-benar hancur (bila perlu)
    DisposableEffect(Unit) {
        onDispose {
            klienHttp.close()
        }
    }
}
