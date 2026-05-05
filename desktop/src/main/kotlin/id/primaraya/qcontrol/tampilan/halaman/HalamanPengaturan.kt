package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tampilan.state.StatusRingkasanOutbox
import id.primaraya.qcontrol.tampilan.state.StatusPenyimpananLokal
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanPengaturan(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    var tampilkanDiagnostik by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        item {
            Text(
                text = "Pusat Pengaturan & Koneksi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kelola koneksi server, penyimpanan lokal, dan sinkronisasi data.",
                style = MaterialTheme.typography.bodyMedium,
                color = TeksAbuAbu
            )
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
        }

        // Status Koneksi Server
        item {
            PanelQControl {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Koneksi PGNServer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Status komunikasi aplikasi dengan server pusat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeksAbuAbu
                        )
                    }
                    val (warnaStatus, labelStatus) = when (keadaan.statusKoneksi) {
                        StatusKoneksiServer.Tersambung -> BerhasilHijau to "Tersambung"
                        StatusKoneksiServer.Memeriksa -> PeringatanKuning to "Memeriksa..."
                        StatusKoneksiServer.Terputus -> GagalMerah to "Terputus"
                        StatusKoneksiServer.TidakDiperiksa -> Color.Gray to "Offline"
                    }
                    ChipStatusQControl(label = labelStatus, warna = warnaStatus)
                }
                
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                PembatasHalusQControl()
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                Row(horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)) {
                    Button(
                        onClick = { onAksi(AksiAplikasi.PeriksaKoneksiServer) },
                        enabled = keadaan.statusKoneksi != StatusKoneksiServer.Memeriksa,
                        shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Periksa Ulang Koneksi")
                    }
                }

                if (tampilkanDiagnostik) {
                    Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(LatarBelakangKonten, RoundedCornerShape(UkuranQControl.RadiusNormal)).padding(UkuranQControl.SpasiNormal),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Detail Diagnostik Server:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        keadaan.statusKesehatanServer?.let {
                            Text(text = "• Versi API: ${it.versiApi}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Status Server: ${it.status}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• DB Server: ${it.koneksiDatabase.status}", style = MaterialTheme.typography.bodySmall)
                        } ?: Text(text = "• Data diagnostik tidak tersedia.", style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu)
                    }
                }
            }
        }

        // Status Penyimpanan Lokal
        item {
            PanelQControl {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Database SQLite Lokal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Status penyimpanan data pada perangkat ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeksAbuAbu
                        )
                    }
                    val (warnaStatusLokal, labelStatusLokal) = when (keadaan.statusDatabaseLokal) {
                        StatusPenyimpananLokal.Tersedia -> BerhasilHijau to "Database OK"
                        StatusPenyimpananLokal.Memeriksa -> PeringatanKuning to "Memeriksa..."
                        else -> GagalMerah to "Bermasalah"
                    }
                    ChipStatusQControl(label = labelStatusLokal, warna = warnaStatusLokal)
                }
                
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                PembatasHalusQControl()
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                Button(
                    onClick = { onAksi(AksiAplikasi.PeriksaDatabaseLokal) },
                    enabled = keadaan.statusDatabaseLokal != StatusPenyimpananLokal.Memeriksa,
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TeksGelap)
                ) {
                    Text("Periksa Database Lokal")
                }

                if (tampilkanDiagnostik) {
                    Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(LatarBelakangKonten, RoundedCornerShape(UkuranQControl.RadiusNormal)).padding(UkuranQControl.SpasiNormal),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Detail Diagnostik Lokal:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        keadaan.informasiDatabaseLokal?.let {
                            Text(text = "• Path: ${it.path}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Versi Skema: ${it.versiSkema}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Ukuran: ${it.ukuranReadable}", style = MaterialTheme.typography.bodySmall)
                        } ?: Text(text = "• Data diagnostik tidak tersedia.", style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu)
                    }
                }
            }
        }

        // Outbox Sinkronisasi
        item {
            PanelQControl {
                Text(
                    text = "Sinkronisasi & Outbox",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pengiriman otomatis data pemeriksaan ke PGNServer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TeksAbuAbu
                )
                
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                
                Row(
                    modifier = Modifier.fillMaxWidth().background(LatarBelakangKonten, RoundedCornerShape(UkuranQControl.RadiusNormal)).padding(UkuranQControl.SpasiNormal),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sinkronisasi Otomatis",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (keadaan.sinkronisasiOtomatisAktif) "Aktif - Data akan dikirim secara periodik." else "Nonaktif - Data hanya dikirim manual.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeksAbuAbu
                        )
                    }
                    Switch(
                        checked = keadaan.sinkronisasiOtomatisAktif,
                        onCheckedChange = { aktif ->
                            if (aktif) onAksi(AksiAplikasi.AktifkanSinkronisasiOtomatis)
                            else onAksi(AksiAplikasi.NonaktifkanSinkronisasiOtomatis)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = BerhasilHijau)
                    )
                }

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                // Status Worker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (keadaan.sedangSinkronisasi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = InfoBiru
                        )
                        Text(
                            text = "Sedang mengirim data ke server...",
                            style = MaterialTheme.typography.bodySmall,
                            color = InfoBiru,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        val warnaWorker = if (keadaan.sinkronisasiOtomatisAktif) BerhasilHijau else TeksAbuAbu
                        Box(
                            modifier = Modifier.size(10.dp).background(warnaWorker, RoundedCornerShape(5.dp))
                        )
                        Text(
                            text = if (keadaan.sinkronisasiOtomatisAktif) "Worker Antrean Aktif (Menunggu)" else "Worker Antrean Nonaktif",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LatarBelakangKonten, RoundedCornerShape(UkuranQControl.RadiusNormal))
                        .padding(UkuranQControl.SpasiNormal),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Ringkasan Antrean Data:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    keadaan.ringkasanOutboxSinkronisasi?.let { r ->
                        Row(horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)) {
                            Column(modifier = Modifier.weight(1f)) {
                                RingkasanItem("Total Item", r.jumlahTotal.toString())
                                RingkasanItem("Menunggu", r.jumlahMenunggu.toString(), if (r.jumlahMenunggu > 0) PeringatanKuning else TeksGelap)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                RingkasanItem("Berhasil", r.jumlahBerhasil.toString(), BerhasilHijau)
                                RingkasanItem("Gagal/Error", (r.jumlahGagal + r.jumlahKonflik).toString(), if (r.jumlahGagal > 0) GagalMerah else TeksGelap)
                            }
                        }
                    } ?: Text("Belum ada data antrean.", style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu)
                }

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiSedang)
                ) {
                    Button(
                        onClick = { onAksi(AksiAplikasi.SinkronkanOutboxSekarang) },
                        modifier = Modifier.weight(1f),
                        enabled = !keadaan.sedangSinkronisasi,
                        shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sinkronkan Sekarang")
                    }
                    
                    OutlinedButton(
                        onClick = { onAksi(AksiAplikasi.ResetOutboxSedangDikirim) },
                        modifier = Modifier.weight(0.6f),
                        enabled = !keadaan.sedangSinkronisasi,
                        shape = RoundedCornerShape(UkuranQControl.RadiusNormal)
                    ) {
                        Text("Reset Stuck")
                    }
                }
            }
        }

        // Diagnostik Toggle
        item {
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { tampilkanDiagnostik = !tampilkanDiagnostik }) {
                    Icon(
                        imageVector = if (tampilkanDiagnostik) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (tampilkanDiagnostik) "Sembunyikan Detail Diagnostik" else "Tampilkan Detail Diagnostik")
                }
            }
        }
    }
}

@Composable
private fun RingkasanItem(
    label: String,
    nilai: String,
    warnaNilai: Color = TeksGelap
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu)
        Text(text = nilai, style = MaterialTheme.typography.bodyMedium, color = warnaNilai, fontWeight = FontWeight.Bold)
    }
}
