package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.komponen.KartuInformasi
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun HalamanDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        Text(
            text = "Selamat Datang di Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
        ) {
            KartuInformasi(
                modifier = Modifier.weight(1f),
                judul = "Total Check"
            ) {
                Text("12.540", style = MaterialTheme.typography.headlineLarge)
                Text("100%", style = MaterialTheme.typography.labelMedium)
            }
            KartuInformasi(
                modifier = Modifier.weight(1f),
                judul = "Total OK"
            ) {
                Text("12.030", style = MaterialTheme.typography.headlineLarge)
                Text("95,93%", style = MaterialTheme.typography.labelMedium)
            }
            KartuInformasi(
                modifier = Modifier.weight(1f),
                judul = "Total Defect"
            ) {
                Text("510", style = MaterialTheme.typography.headlineLarge)
                Text("4,07%", style = MaterialTheme.typography.labelMedium)
            }
        }
        
        KartuInformasi(judul = "Informasi") {
            Text("Fitur monitoring ringkasan data defect produksi akan tampil di sini.")
        }
    }
}
