package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi

@Composable
fun KerangkaAplikasi(
    keadaan: KeadaanAplikasi,
    onPilihRute: (RuteAplikasi) -> Unit,
    onPeriksaKoneksi: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        SidebarAplikasi(
            keadaan = keadaan,
            onPilihRute = onPilihRute
        )

        Scaffold(
            topBar = {
                HeaderAplikasi(
                    keadaan = keadaan,
                    onPeriksaKoneksi = onPeriksaKoneksi
                )
            }
        ) { padding ->
            KontenAplikasi(
                keadaan = keadaan,
                padding = padding
            )
        }
    }
}
