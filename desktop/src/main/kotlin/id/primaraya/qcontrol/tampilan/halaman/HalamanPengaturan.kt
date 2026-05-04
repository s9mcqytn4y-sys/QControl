package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tampilan.state.StatusRingkasanOutbox
import id.primaraya.qcontrol.tampilan.state.StatusPenyimpananLokal
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi

@Composable
fun HalamanPengaturan(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan",
            style = MaterialTheme.typography.headlineMedium
        )

        // Status Koneksi Server
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Status Server (PGNServer)",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val warnaStatus = when (keadaan.statusKoneksi) {
                        StatusKoneksiServer.Tersambung -> MaterialTheme.colorScheme.primary
                        StatusKoneksiServer.Memeriksa -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(warnaStatus, RoundedCornerShape(6.dp))
                    )
                    Text(text = keadaan.pesanStatusKoneksi)
                }
                
                keadaan.statusKesehatanServer?.let {
                    Text(text = "Versi API: ${it.versiApi}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Status: ${it.status}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Database Server: ${it.koneksiDatabase.status} (${it.koneksiDatabase.driver})", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAksi(AksiAplikasi.PeriksaKoneksiServer) },
                    enabled = keadaan.statusKoneksi != StatusKoneksiServer.Memeriksa
                ) {
                    Text("Periksa Koneksi")
                }
            }
        }

        // Status Penyimpanan Lokal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Status Penyimpanan Lokal",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val warnaStatusLokal = when (keadaan.statusDatabaseLokal) {
                        StatusPenyimpananLokal.Tersedia -> MaterialTheme.colorScheme.primary
                        StatusPenyimpananLokal.Memeriksa -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(warnaStatusLokal, RoundedCornerShape(6.dp))
                    )
                    Text(text = keadaan.pesanStatusDatabaseLokal ?: "")
                }
                
                keadaan.informasiDatabaseLokal?.let {
                    Text(text = "Path: ${it.path}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Versi Skema: ${it.versiSkema}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Ukuran: ${it.ukuranReadable}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAksi(AksiAplikasi.PeriksaDatabaseLokal) },
                    enabled = keadaan.statusDatabaseLokal != StatusPenyimpananLokal.Memeriksa
                ) {
                    Text("Periksa Database Lokal")
                }
            }
        }

        // Outbox Sinkronisasi
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Manajemen Antrean (Outbox)",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sinkronisasi Otomatis",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Kirim data ke server secara periodik",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = keadaan.sinkronisasiOtomatisAktif,
                        onCheckedChange = { aktif ->
                            if (aktif) onAksi(AksiAplikasi.AktifkanSinkronisasiOtomatis)
                            else onAksi(AksiAplikasi.NonaktifkanSinkronisasiOtomatis)
                        }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                // Status Worker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (keadaan.sedangSinkronisasi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Sedang mensinkronkan...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    if (keadaan.sinkronisasiOtomatisAktif) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.outline, 
                                    RoundedCornerShape(6.dp)
                                )
                        )
                        Text(
                            text = if (keadaan.sinkronisasiOtomatisAktif) "Worker Aktif (Idle)" else "Worker Nonaktif",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    keadaan.ringkasanOutboxSinkronisasi?.let { r ->
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                RingkasanItem("Total", r.jumlahTotal.toString())
                                RingkasanItem("Menunggu", r.jumlahMenunggu.toString())
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                RingkasanItem("Berhasil", r.jumlahBerhasil.toString(), MaterialTheme.colorScheme.primary)
                                RingkasanItem("Gagal/Error", (r.jumlahGagal + r.jumlahKonflik).toString(), MaterialTheme.colorScheme.error)
                            }
                        }
                        
                        if (r.jumlahSedangDikirim > 0) {
                            Text(
                                text = "${r.jumlahSedangDikirim} item sedang diproses...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Sinkronisasi Terakhir: ${keadaan.waktuSinkronisasiTerakhir ?: "-"}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    keadaan.pesanSinkronisasiTerakhir?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAksi(AksiAplikasi.SinkronkanOutboxSekarang) },
                        modifier = Modifier.weight(1f),
                        enabled = !keadaan.sedangSinkronisasi
                    ) {
                        Text("Sinkronkan Sekarang")
                    }
                    
                    OutlinedButton(
                        onClick = { onAksi(AksiAplikasi.ResetOutboxSedangDikirim) },
                        modifier = Modifier.weight(0.6f),
                        enabled = !keadaan.sedangSinkronisasi
                    ) {
                        Text("Reset Stuck")
                    }
                }

                OutlinedButton(
                    onClick = { onAksi(AksiAplikasi.BuatContohItemOutboxUntukPengujian) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !keadaan.sedangSinkronisasi
                ) {
                    Text("Tambah Test Item (Simulasi)")
                }
            }
        }
    }
}

@Composable
private fun RingkasanItem(
    label: String,
    nilai: String,
    warnaNilai: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = nilai, style = MaterialTheme.typography.bodySmall, color = warnaNilai)
    }
}
