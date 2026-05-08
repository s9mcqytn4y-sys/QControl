package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.komponen.ChipStatusQControl
import id.primaraya.qcontrol.tampilan.komponen.PanelPremiumQControl
import id.primaraya.qcontrol.tampilan.komponen.StateKosongQControl
import id.primaraya.qcontrol.tampilan.mvi.InputHarianIntent
import id.primaraya.qcontrol.tampilan.mvi.MasterDataIntent
import id.primaraya.qcontrol.tampilan.mvi.ShellIntent
import id.primaraya.qcontrol.tampilan.mvi.SinkronisasiIntent
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.AplikasiGraph
import id.primaraya.qcontrol.tema.BerhasilHijau
import id.primaraya.qcontrol.tema.GagalMerah
import id.primaraya.qcontrol.tema.InfoBiru
import id.primaraya.qcontrol.tema.PeringatanKuning
import id.primaraya.qcontrol.tema.TeksAbuAbu
import id.primaraya.qcontrol.tema.TeksGelap
import id.primaraya.qcontrol.tema.TeksKontrasRendah
import id.primaraya.qcontrol.tema.TeksKontrasTinggi
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun HalamanDashboard(graph: AplikasiGraph) {
    val masterDataState by graph.masterDataStore.state.collectAsState()
    val inputHarianState by graph.inputHarianStore.state.collectAsState()
    val sinkronisasiState by graph.sinkronisasiStore.state.collectAsState()

    LaunchedEffect(Unit) {
        graph.masterDataStore.tangani(MasterDataIntent.MuatLokal)
        graph.sinkronisasiStore.tangani(SinkronisasiIntent.MuatRingkasan)
    }

    LaunchedEffect(masterDataState.daftarLineProduksi, inputHarianState.draft?.id) {
        val lineAwal = masterDataState.daftarLineProduksi.firstOrNull()?.id
        if (lineAwal != null && inputHarianState.draft == null) {
            graph.inputHarianStore.tangani(
                InputHarianIntent.Inisialisasi(
                    tanggal = java.time.LocalDate.now().toString(),
                    lineId = lineAwal
                )
            )
        }
    }

    val ringkasanHariIni by remember(inputHarianState.ringkasan) {
        derivedStateOf { inputHarianState.ringkasan }
    }
    val totalCheck by remember(ringkasanHariIni) {
        derivedStateOf { ringkasanHariIni?.totalQtyCheck ?: 0 }
    }
    val totalDefect by remember(ringkasanHariIni) {
        derivedStateOf { ringkasanHariIni?.totalQtyDefect ?: 0 }
    }
    val totalOk by remember(totalCheck, totalDefect) {
        derivedStateOf { (totalCheck - totalDefect).coerceAtLeast(0) }
    }
    val rasioDefect by remember(totalCheck, totalDefect) {
        derivedStateOf {
            if (totalCheck == 0) 0.0 else (totalDefect.toDouble() / totalCheck.toDouble()) * 100.0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ringkasan Hari Ini",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Performa kualitas produksi dari penyimpanan lokal hari ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TeksAbuAbu
                )
            }
            ChipStatusQControl(
                label = "Status Data Acuan: ${masterDataState.ringkasan?.versiMasterData ?: "Belum tersedia"}",
                warna = if (masterDataState.ringkasan != null) BerhasilHijau else PeringatanKuning
            )
        }

        if (masterDataState.ringkasan == null) {
            StateKosongQControl(
                ikon = Icons.Default.Storage,
                judul = "Data Acuan Belum Tersedia",
                pesan = "Tarik data acuan dari server perusahaan terlebih dahulu agar ringkasan harian dapat dihitung dari cache lokal.",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onAksi = { graph.shellStore.tangani(ShellIntent.PilihRute(RuteAplikasi.MasterData)) },
                labelAksi = "Buka Data Acuan"
            )
            return
        }

        if (inputHarianState.sedangMemuat && inputHarianState.draft == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Memuat ringkasan hari ini...", color = TeksAbuAbu)
                }
            }
            return
        }

        if (totalCheck == 0 && totalDefect == 0) {
            StateKosongQControl(
                ikon = Icons.Default.Info,
                judul = "Ringkasan Hari Ini Belum Tersedia",
                pesan = "Belum ada draft inspeksi yang berisi hasil pemeriksaan hari ini. Mulai pencatatan dari Input Harian agar dashboard menampilkan data nyata.",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onAksi = { graph.shellStore.tangani(ShellIntent.PilihRute(RuteAplikasi.InputHarian)) },
                labelAksi = "Buka Input Harian"
            )
            return
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = InfoBiru.copy(alpha = 0.1f),
            shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
            border = androidx.compose.foundation.BorderStroke(1.dp, InfoBiru.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(UkuranQControl.SpasiNormal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = InfoBiru)
                Spacer(modifier = Modifier.width(UkuranQControl.SpasiNormal))
                Text(
                    text = "Ringkasan ini dibaca langsung dari draft inspeksi lokal untuk tanggal hari ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InfoBiru
                )
            }
        }

        sinkronisasiState.ringkasan?.let { ringkasanSinkronisasi ->
            if (ringkasanSinkronisasi.jumlahKonflik > 0 || ringkasanSinkronisasi.jumlahGagal > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PeringatanKuning.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PeringatanKuning.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(UkuranQControl.SpasiNormal)) {
                        Text(
                            text = "Perlu tindak lanjut pengiriman",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TeksKontrasTinggi
                        )
                        Text(
                            text = "Antrean gagal ${ringkasanSinkronisasi.jumlahGagal} dan konflik ${ringkasanSinkronisasi.jumlahKonflik}. Tinjau data sebelum mencoba kirim ulang.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeksKontrasRendah
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
        ) {
            KartuDashboardReal(modifier = Modifier.weight(1f), judul = "Total Check", nilai = totalCheck.toString(), warna = TeksGelap)
            KartuDashboardReal(modifier = Modifier.weight(1f), judul = "Total OK", nilai = totalOk.toString(), warna = BerhasilHijau)
            KartuDashboardReal(modifier = Modifier.weight(1f), judul = "Total Defect", nilai = totalDefect.toString(), warna = GagalMerah)
            KartuDashboardReal(
                modifier = Modifier.weight(1f),
                judul = "Rasio Defect",
                nilai = String.format("%.2f%%", rasioDefect),
                warna = if (rasioDefect > 5.0) GagalMerah else PeringatanKuning
            )
        }

        PanelPremiumQControl(modifier = Modifier.weight(1f), judul = "Sorotan Hari Ini") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoSorotan("Part sudah diisi", ringkasanHariIni?.totalPartSudahDiisi?.toString() ?: "0")
                InfoSorotan("Part belum diisi", ringkasanHariIni?.totalPartBelumDiisi?.toString() ?: "0")
                val defectUtama = ringkasanHariIni?.daftarDefect?.firstOrNull()
                InfoSorotan(
                    "Defect tertinggi",
                    defectUtama?.let { "${it.namaDefect} (${it.jumlah})" } ?: "Belum ada temuan"
                )
                val slotPuncak = ringkasanHariIni?.daftarPerSlot?.maxByOrNull { it.jumlah }
                InfoSorotan(
                    "Slot paling padat",
                    slotPuncak?.let { "${it.labelSlot} (${it.jumlah})" } ?: "Belum ada catatan slot"
                )
            }
        }
    }
}

@Composable
private fun KartuDashboardReal(
    modifier: Modifier = Modifier,
    judul: String,
    nilai: String,
    warna: Color
) {
    PanelPremiumQControl(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = judul, style = MaterialTheme.typography.labelMedium, color = TeksAbuAbu)
            Text(text = nilai, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = warna)
        }
    }
}

@Composable
private fun InfoSorotan(label: String, nilai: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TeksKontrasRendah
        )
        Text(
            text = nilai,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TeksKontrasTinggi
        )
    }
}
