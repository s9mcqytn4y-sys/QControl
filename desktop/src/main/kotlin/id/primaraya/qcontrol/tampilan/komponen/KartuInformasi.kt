package id.primaraya.qcontrol.tampilan.komponen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun KartuInformasi(
    modifier: Modifier = Modifier,
    judul: String,
    konten: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = UkuranQControl.RadiusKecil)
    ) {
        Column(
            modifier = Modifier.padding(UkuranQControl.SpasiNormal)
        ) {
            Text(
                text = judul,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(UkuranQControl.SpasiSedang))
            konten()
        }
    }
}
