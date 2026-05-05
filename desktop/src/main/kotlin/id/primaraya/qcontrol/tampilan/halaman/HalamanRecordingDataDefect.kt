package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.runtime.Composable
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi

@Composable
fun HalamanRecordingDataDefect(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    HalamanGeneric(keadaan, onAksi)
}
