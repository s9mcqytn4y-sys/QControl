package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.state.AplikasiGraph
import id.primaraya.qcontrol.tampilan.state.TabMasterData
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanMasterData(graph: AplikasiGraph) {
    val state by graph.masterDataStore.state.collectAsState()
    
    LaunchedEffect(Unit) {
        graph.masterDataStore.tangani(MasterDataIntent.MuatLokal)
    }

    Column(modifier = Modifier.fillMaxSize().padding(UkuranQControl.SpasiNormal)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Data Acuan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = TeksKontrasTinggi)
                Text("Data referensi yang tersinkron dari server perusahaan.", style = MaterialTheme.typography.bodySmall, color = TeksKontrasRendah)
            }
            
            TombolUtamaQControl(
                text = "Perbarui Data Acuan",
                onClick = { graph.masterDataStore.tangani(MasterDataIntent.TarikDariServer) },
                ikon = Icons.Default.Refresh,
                sedangMemuat = state.sedangMemuat
            )
        }

        Spacer(Modifier.height(UkuranQControl.SpasiBesar))

        PanelPremiumQControl(modifier = Modifier.weight(1f)) {
            Column {
                ScrollableTabRow(
                    selectedTabIndex = state.tabAktif.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    TabMasterData.values().forEach { tab ->
                        Tab(
                            selected = state.tabAktif == tab,
                            onClick = { graph.masterDataStore.tangani(MasterDataIntent.PilihTab(tab)) },
                            text = { Text(tab.label, style = MaterialTheme.typography.labelLarge) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (state.tabAktif != TabMasterData.RINGKASAN) {
                    OutlinedTextField(
                        value = state.kataKunci,
                        onValueChange = { graph.masterDataStore.tangani(MasterDataIntent.Cari(it)) },
                        placeholder = { Text("Cari...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, null) }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (state.tabAktif) {
                        TabMasterData.RINGKASAN -> RingkasanMasterContent(state)
                        TabMasterData.PART -> ListMasterContent(state.daftarPart) { it.namaPart }
                        TabMasterData.JENIS_DEFECT -> ListMasterContent(state.daftarJenisDefect) { it.namaDefect }
                        TabMasterData.MATERIAL -> ListMasterContent(state.daftarMaterial) { it.namaMaterial }
                        TabMasterData.SLOT_WAKTU -> ListMasterContent(state.daftarSlotWaktu) { it.labelSlot }
                        TabMasterData.LINE_PRODUKSI -> ListMasterContent(state.daftarLineProduksi) { it.namaLine }
                    }
                }
            }
        }
    }
}

@Composable
private fun RingkasanMasterContent(state: MasterDataState) {
    val r = state.ringkasan
    if (r == null) {
        Text("Data belum tersedia")
        return
    }
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { InfoRow("Versi Data", r.versiMasterData) }
        item { InfoRow("Terakhir Tarik", r.ditarikPada) }
        item { InfoRow("Total Part", r.jumlahPart.toString()) }
        item { InfoRow("Total Line", r.jumlahLineProduksi.toString()) }
        item { InfoRow("Total Jenis Defect", r.jumlahJenisDefect.toString()) }
    }
}

@Composable
private fun <T> ListMasterContent(items: List<T>, labelMapper: (T) -> String) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(items) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LatarBelakangUtama.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(labelMapper(item), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, nilai: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TeksKontrasRendah)
        Text(nilai, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
    }
}
