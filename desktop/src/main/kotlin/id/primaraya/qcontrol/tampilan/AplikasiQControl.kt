package id.primaraya.qcontrol.tampilan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.kerangka.KerangkaAplikasi
import id.primaraya.qcontrol.tema.TemaQControl

@Composable
fun AplikasiQControl() {
    TemaQControl {
        KerangkaAplikasi(judul = "Dashboard") { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Selamat Datang di QControl",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Pilih menu di samping untuk memulai.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
