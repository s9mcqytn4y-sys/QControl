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
import id.primaraya.qcontrol.tampilan.state.StatusPenyimpananLokal

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
    }
}
