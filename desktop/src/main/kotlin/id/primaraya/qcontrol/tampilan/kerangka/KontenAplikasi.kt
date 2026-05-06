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
            RuteAplikasi.InputHarian -> HalamanInputHarian(keadaan = keadaan, onAksi = onAksi)
            RuteAplikasi.RecordingDataDefect -> HalamanRecordingDataDefect(keadaan = keadaan, onAksi = onAksi)
            RuteAplikasi.ControlChart -> HalamanControlChart(keadaan = keadaan, onAksi = onAksi)
            RuteAplikasi.LaporanBulanan -> HalamanLaporanBulanan(keadaan = keadaan, onAksi = onAksi)
            RuteAplikasi.MasterData -> HalamanMasterData(keadaan = keadaan, onAksi = onAksi)
            RuteAplikasi.Pengaturan -> HalamanPengaturan(keadaan = keadaan, onAksi = onAksi)
        }
    }
}
