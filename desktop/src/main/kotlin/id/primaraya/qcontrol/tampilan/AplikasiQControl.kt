package id.primaraya.qcontrol.tampilan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import id.primaraya.qcontrol.tampilan.kerangka.KerangkaAplikasi
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.PengelolaKeadaanAplikasi
import id.primaraya.qcontrol.tema.TemaQControl

@Composable
fun AplikasiQControl() {
    val pengelolaState = remember { PengelolaKeadaanAplikasi() }
    val keadaan by pengelolaState.keadaan.collectAsState()

    TemaQControl {
        KerangkaAplikasi(
            keadaan = keadaan,
            onPilihRute = { rute ->
                pengelolaState.tangani(AksiAplikasi.PilihRute(rute))
            }
        )
    }
}
