package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.halaman.*
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi

@Composable
fun KontenAplikasi(
    keadaan: KeadaanAplikasi,
    padding: PaddingValues,
    onAksi: (AksiAplikasi) -> Unit
) {
    Box(modifier = Modifier.padding(padding)) {
        when (keadaan.ruteAktif) {
            RuteAplikasi.Dashboard -> HalamanDashboard()
            RuteAplikasi.InputHarian -> HalamanInputHarian()
            RuteAplikasi.RecordingDataDefect -> HalamanRecordingDataDefect()
            RuteAplikasi.ControlChart -> HalamanControlChart()
            RuteAplikasi.LaporanBulanan -> HalamanLaporanBulanan()
            RuteAplikasi.MasterData -> HalamanMasterData()
            RuteAplikasi.Pengaturan -> HalamanPengaturan(keadaan = keadaan, onAksi = onAksi)
        }
    }
}
