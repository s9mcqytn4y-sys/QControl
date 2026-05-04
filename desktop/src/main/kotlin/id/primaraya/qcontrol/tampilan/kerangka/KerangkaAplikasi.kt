package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi

@Composable
fun KerangkaAplikasi(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        SidebarAplikasi(
            keadaan = keadaan,
            onPilihRute = { onAksi(AksiAplikasi.PilihRute(it)) },
            onLogout = { onAksi(AksiAplikasi.Logout) }
        )

        Scaffold(
            topBar = {
                HeaderAplikasi(
                    keadaan = keadaan,
                    onPeriksaKoneksi = { onAksi(AksiAplikasi.PeriksaKoneksiServer) }
                )
            }
        ) { padding ->
            KontenAplikasi(
                keadaan = keadaan,
                padding = padding,
                onAksi = onAksi
            )
        }
    }
}
