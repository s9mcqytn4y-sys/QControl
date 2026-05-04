package id.primaraya.qcontrol.tampilan

import androidx.compose.runtime.*
import id.primaraya.qcontrol.data.remote.http.buatKlienHttpAplikasi
import id.primaraya.qcontrol.data.remote.layanan.LayananKesehatanServerRemote
import id.primaraya.qcontrol.data.repositori.RepositoriKesehatanServer
import id.primaraya.qcontrol.ranah.usecase.PeriksaKesehatanServerUseCase
import id.primaraya.qcontrol.tampilan.kerangka.KerangkaAplikasi
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.PengelolaKeadaanAplikasi
import id.primaraya.qcontrol.tema.TemaQControl

@Composable
fun AplikasiQControl() {
    // Inisialisasi Dependensi (Manual DI untuk fase ini)
    val klienHttp = remember { buatKlienHttpAplikasi() }
    val layananRemote = remember { LayananKesehatanServerRemote(klienHttp) }
    val repositori = remember { RepositoriKesehatanServer(layananRemote) }
    val useCase = remember { PeriksaKesehatanServerUseCase(repositori) }
    
    val pengelolaState = remember { PengelolaKeadaanAplikasi(useCase) }
    val keadaan by pengelolaState.keadaan.collectAsState()

    // Jalankan periksa koneksi saat pertama kali buka
    LaunchedEffect(Unit) {
        pengelolaState.tangani(AksiAplikasi.PeriksaKoneksiServer)
    }

    TemaQControl {
        KerangkaAplikasi(
            keadaan = keadaan,
            onPilihRute = { rute ->
                pengelolaState.tangani(AksiAplikasi.PilihRute(rute))
            },
            onPeriksaKoneksi = {
                pengelolaState.tangani(AksiAplikasi.PeriksaKoneksiServer)
            }
        )
    }
    
    // Pastikan menutup HTTP client saat aplikasi benar-benar hancur (bila perlu)
    DisposableEffect(Unit) {
        onDispose {
            klienHttp.close()
        }
    }
}
