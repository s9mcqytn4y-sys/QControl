package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tema.UkuranQControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderAplikasi(keadaan: KeadaanAplikasi) {
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Sync, contentDescription = "Sinkronisasi")
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
