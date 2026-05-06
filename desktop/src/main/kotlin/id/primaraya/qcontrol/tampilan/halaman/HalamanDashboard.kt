package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dashboard Produksi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ringkasan performa kualitas line produksi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TeksAbuAbu
                )
            }
            ChipStatusQControl(label = "DATA SIMULASI", warna = PeringatanKuning)
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = InfoBiru.copy(alpha = 0.1f),
            shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
            border = androidx.compose.foundation.BorderStroke(1.dp, InfoBiru.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(UkuranQControl.SpasiNormal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = InfoBiru)
                Spacer(modifier = Modifier.width(UkuranQControl.SpasiNormal))
                Text(
                    text = "Dashboard analitik akan aktif setelah data pemeriksaan harian tersinkron ke PGNServer. Saat ini sistem menampilkan data contoh untuk keperluan demo UI.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InfoBiru
                )
            }
        }

        // Dummy stats placeholders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
        ) {
            KartuDashboardDummy(modifier = Modifier.weight(1f), judul = "Total Check", nilai = "1,250", warna = TeksGelap)
            KartuDashboardDummy(modifier = Modifier.weight(1f), judul = "Total OK", nilai = "1,215", warna = BerhasilHijau)
            KartuDashboardDummy(modifier = Modifier.weight(1f), judul = "Total NG", nilai = "35", warna = GagalMerah)
            KartuDashboardDummy(modifier = Modifier.weight(1f), judul = "Reject Rate", nilai = "2.8%", warna = PeringatanKuning)
        }
        
        PanelPremiumQControl(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Visualisasi grafik performa mingguan akan tampil di sini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TeksAbuAbu
                )
            }
        }
    }
}

@Composable
private fun KartuDashboardDummy(
    modifier: Modifier = Modifier,
    judul: String,
    nilai: String,
    warna: Color
) {
    PanelPremiumQControl(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = judul, style = MaterialTheme.typography.labelMedium, color = TeksAbuAbu)
            Text(text = nilai, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = warna)
        }
    }
}
