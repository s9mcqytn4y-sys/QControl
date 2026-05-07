package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.background
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
import id.primaraya.qcontrol.tampilan.komponen.ChipStatusQControl
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tema.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderAplikasi(
    keadaan: KeadaanAplikasi,
    onPeriksaKoneksi: () -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .height(UkuranQControl.TinggiHeader)
            .background(LatarBelakangSidebar.copy(alpha = 0.8f)),
        title = {
            Column {
                Text(
                    text = keadaan.ruteAktif.judul,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TeksKontrasTinggi
                )
                Text(
                    text = keadaan.ruteAktif.deskripsi,
                    style = MaterialTheme.typography.labelSmall,
                    color = TeksKontrasRendah
                )
            }
        },
        actions = {
            // Status Koneksi & Sesi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UkuranQControl.SpasiNormal),
                horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiSedang)
            ) {
                // Sesi HeadQC
                if (keadaan.sesiHeadQCTidakValid) {
                    ChipStatusQControl(
                        label = "Sesi Berakhir",
                        warna = GagalMerah
                    )
                } else {
                    ChipStatusQControl(
                        label = "Sesi HeadQC Aktif",
                        warna = BerhasilHijau
                    )
                }

                // Status Server
                val (warnaServer, labelServer) = when (keadaan.statusKoneksi) {
                    StatusKoneksiServer.Tersambung -> BerhasilHijau to "PGNServer Tersambung"
                    StatusKoneksiServer.Terputus -> PeringatanKuning to "Mode Offline"
                    StatusKoneksiServer.Memeriksa -> PeringatanKuning to "Memeriksa..."
                    StatusKoneksiServer.TidakDiperiksa -> TeksKontrasRendah to "Mode Offline"
                }
                
                ChipStatusQControl(
                    label = labelServer,
                    warna = warnaServer
                )
            }

            IconButton(onClick = onPeriksaKoneksi) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Periksa Koneksi",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = TeksKontrasRendah)
            }
            Spacer(Modifier.width(UkuranQControl.SpasiNormal))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = TeksKontrasTinggi,
            actionIconContentColor = TeksKontrasTinggi
        )
    )
}
