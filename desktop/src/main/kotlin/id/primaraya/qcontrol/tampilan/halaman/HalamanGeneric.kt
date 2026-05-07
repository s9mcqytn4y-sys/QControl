package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import id.primaraya.qcontrol.tampilan.komponen.StateKosongQControl
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanGeneric(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(LatarBelakangKonten),
        contentAlignment = Alignment.Center
    ) {
        StateKosongQControl(
            ikon = Icons.Default.Construction,
            judul = "Halaman Sedang Dikembangkan",
            pesan = "Fitur untuk ${keadaan.ruteAktif.labelMenu} akan tersedia pada fase pengembangan berikutnya.",
            onAksi = { onAksi(AksiAplikasi.PilihRute(id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi.Dashboard)) },
            labelAksi = "Kembali ke Dashboard"
        )
    }
}
