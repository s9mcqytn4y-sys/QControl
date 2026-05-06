package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tampilan.state.StatusPenyimpananLokal
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanPengaturan(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    var tampilkanDiagnostik by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiBesar)
    ) {
        // --- HEADER ---
        item {
            HeaderHalamanQControl(
                judul = "Pusat Pengaturan",
                subtitle = "Konfigurasi koneksi server, sesi pengguna, dan sinkronisasi data lokal."
            )
        }

        // --- GRID LAYOUT UNTUK PENGATURAN ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)) {
                // Sesi & Server
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)) {
                    SectionServer(keadaan, onAksi)
                    SectionSesi(keadaan, onAksi)
                }
                
                // Database & Sinkronisasi
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)) {
                    SectionDatabase(keadaan, onAksi)
                    SectionSinkronisasi(keadaan, onAksi)
                }
            }
        }

        // --- DIAGNOSTIK TOGGLE ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                PembatasHalusQControl()
                Spacer(Modifier.height(UkuranQControl.SpasiBesar))
                TextButton(onClick = { tampilkanDiagnostik = !tampilkanDiagnostik }) {
                    Icon(if (tampilkanDiagnostik) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(if (tampilkanDiagnostik) "Sembunyikan Diagnostik" else "Tampilkan Detail Diagnostik")
                }
                
                if (tampilkanDiagnostik) {
                    Spacer(Modifier.height(16.dp))
                    PanelPremiumQControl(judul = "Informasi Diagnostik Sistem") {
                        DiagnostikItem("Endpoint API", "http://127.0.0.1:8000/api/v1")
                        DiagnostikItem("Path Database", keadaan.informasiDatabaseLokal?.path ?: "-")
                        DiagnostikItem("Versi Skema", keadaan.informasiDatabaseLokal?.versiSkema?.toString() ?: "-")
                        DiagnostikItem("Ukuran File", keadaan.informasiDatabaseLokal?.ukuranReadable ?: "-")
                        DiagnostikItem("Identitas Sesi", keadaan.sesiAktif?.token?.take(20) + "...")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionServer(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    PanelPremiumQControl(judul = "Koneksi PGNServer") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            val (warna, label) = when (keadaan.statusKoneksi) {
                StatusKoneksiServer.Tersambung -> BerhasilHijau to "TERSAMBUNG"
                StatusKoneksiServer.Terputus -> GagalMerah to "TERPUTUS"
                StatusKoneksiServer.Memeriksa -> PeringatanKuning to "MEMERIKSA"
                StatusKoneksiServer.TidakDiperiksa -> TeksKontrasRendah to "OFFLINE"
            }
            Text("Status Server Pusat", style = MaterialTheme.typography.bodyMedium, color = TeksKontrasTinggi)
            ChipStatusQControl(label = label, warna = warna)
        }
        Spacer(Modifier.height(16.dp))
        TombolUtamaQControl(
            text = "Periksa Koneksi",
            onClick = { onAksi(AksiAplikasi.PeriksaKoneksiServer) },
            ikon = Icons.Default.Refresh,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionSesi(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    PanelPremiumQControl(judul = "Sesi HeadQC") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = MaterialTheme.shapes.small, color = SolarYellow.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = SolarYellow)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(keadaan.sesiAktif?.namaPengguna ?: "Belum Login", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
                Text(keadaan.sesiAktif?.email ?: "-", style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
            }
        }
        Spacer(Modifier.height(16.dp))
        TombolSekunderQControl(
            text = "Keluar Sesi (Logout)",
            onClick = { onAksi(AksiAplikasi.Logout) },
            ikon = Icons.AutoMirrored.Filled.Logout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionDatabase(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    PanelPremiumQControl(judul = "Database SQLite Lokal") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            val (warna, label) = when (keadaan.statusDatabaseLokal) {
                StatusPenyimpananLokal.Tersedia -> BerhasilHijau to "DATABASE OK"
                StatusPenyimpananLokal.Gagal -> GagalMerah to "ERROR"
                StatusPenyimpananLokal.Memeriksa -> PeringatanKuning to "MEMERIKSA"
                StatusPenyimpananLokal.TidakDiperiksa -> TeksKontrasRendah to "BELUM DICEK"
            }
            Text("Penyimpanan Lokal", style = MaterialTheme.typography.bodyMedium, color = TeksKontrasTinggi)
            ChipStatusQControl(label = label, warna = warna)
        }
        Spacer(Modifier.height(16.dp))
        TombolSekunderQControl(
            text = "Validasi Integritas Data",
            onClick = { onAksi(AksiAplikasi.PeriksaDatabaseLokal) },
            ikon = Icons.Default.Storage,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionSinkronisasi(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    PanelPremiumQControl(judul = "Antrean Sinkronisasi (Outbox)") {
        val outbox = keadaan.ringkasanOutboxSinkronisasi
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KartuKecilInfo(Modifier.weight(1f), "Menunggu", outbox?.jumlahMenunggu?.toString() ?: "0", PeringatanKuning)
            KartuKecilInfo(Modifier.weight(1f), "Berhasil", outbox?.jumlahBerhasil?.toString() ?: "0", BerhasilHijau)
            KartuKecilInfo(Modifier.weight(1f), "Gagal", (outbox?.let { it.jumlahGagal + it.jumlahKonflik } ?: 0).toString(), GagalMerah)
        }
        
        Spacer(Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TombolUtamaQControl(
                text = "Sinkronkan",
                onClick = { onAksi(AksiAplikasi.SinkronkanOutboxSekarang) },
                ikon = Icons.Default.CloudSync,
                modifier = Modifier.weight(1f),
                sedangMemuat = keadaan.sedangSinkronisasi
            )
            TombolSekunderQControl(
                text = "Reset Stuck",
                onClick = { onAksi(AksiAplikasi.ResetOutboxSedangDikirim) },
                ikon = Icons.Default.RestartAlt,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

@Composable
private fun KartuKecilInfo(modifier: Modifier, label: String, nilai: String, warna: Color) {
    Surface(modifier = modifier, shape = MaterialTheme.shapes.small, color = LatarBelakangUtama, border = androidx.compose.foundation.BorderStroke(1.dp, GarisHalus)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah, fontWeight = FontWeight.Bold)
            Text(nilai, style = MaterialTheme.typography.titleMedium, color = warna, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun DiagnostikItem(label: String, nilai: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TeksKontrasRendah)
        Text(nilai, style = MaterialTheme.typography.bodySmall, color = TeksKontrasSedang, fontWeight = FontWeight.Bold)
    }
}
