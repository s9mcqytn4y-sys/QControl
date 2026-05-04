package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun HalamanGeneric(rute: RuteAplikasi) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = rute.judul,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(UkuranQControl.SpasiNormal))
            Text(
                text = rute.deskripsi,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(UkuranQControl.SpasiSangatBesar))
            Text(
                text = "Fitur ini sedang dalam tahap pengembangan.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
