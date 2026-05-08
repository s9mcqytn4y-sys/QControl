package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.halaman.HalamanDashboard
import id.primaraya.qcontrol.tampilan.halaman.HalamanInputHarian
import id.primaraya.qcontrol.tampilan.halaman.HalamanMasterData
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.AplikasiGraph

@Composable
fun KontenAplikasi(
    graph: AplikasiGraph,
    padding: PaddingValues
) {
    val shellState by graph.shellStore.state.collectAsState()

    Box(modifier = Modifier.padding(padding)) {
        when (shellState.ruteAktif) {
            RuteAplikasi.Dashboard -> HalamanDashboard(graph = graph)
            RuteAplikasi.InputHarian -> HalamanInputHarian(graph = graph)
            RuteAplikasi.MasterData -> HalamanMasterData(graph = graph)
        }
    }
}
