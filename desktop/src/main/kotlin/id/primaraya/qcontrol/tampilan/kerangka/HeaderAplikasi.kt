package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tema.UkuranQControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderAplikasi(
    keadaan: KeadaanAplikasi,
    onPeriksaKoneksi: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.height(UkuranQControl.TinggiHeader),
        title = {
            Column {
                Text(
                    text = keadaan.ruteAktif.judul,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = keadaan.ruteAktif.deskripsi,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        actions = {
            // Status Koneksi & Sesi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UkuranQControl.SpasiNormal)
            ) {
                // Sesi HeadQC
                if (keadaan.sesiHeadQCTidakValid) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text("Sesi Berakhir", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(8.dp))
                } else {
                    Badge(containerColor = Color(0xFF10B981).copy(alpha = 0.2f)) {
                        Text("Sesi Valid", color = Color(0xFF065F46), style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(8.dp))
                }

                val warnaStatus = when (keadaan.statusKoneksi) {
                    StatusKoneksiServer.Tersambung -> Color(0xFF10B981) // Emerald 500
                    StatusKoneksiServer.Terputus -> Color(0xFFEF4444) // Red 500
                    StatusKoneksiServer.Memeriksa -> Color(0xFFF59E0B) // Amber 500
                    StatusKoneksiServer.TidakDiperiksa -> Color.Gray
                }
                
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = warnaStatus
                ) {}
                Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                Text(
                    text = if (keadaan.pesanStatusKoneksi.isEmpty()) "Tidak diperiksa" else keadaan.pesanStatusKoneksi,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            IconButton(onClick = onPeriksaKoneksi) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Periksa Koneksi",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifikasi")
            }
            Spacer(Modifier.width(UkuranQControl.SpasiNormal))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}
