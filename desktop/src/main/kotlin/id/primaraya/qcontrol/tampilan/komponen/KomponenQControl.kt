package id.primaraya.qcontrol.tampilan.komponen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tema.*

@Composable
fun PanelQControl(
    modifier: Modifier = Modifier,
    warnaLatar: Color = MaterialTheme.colorScheme.surface,
    konten: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
        color = warnaLatar,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(UkuranQControl.SpasiNormal),
            content = konten
        )
    }
}

@Composable
fun ChipStatusQControl(
    label: String,
    warna: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = warna.copy(alpha = 0.1f),
        shape = CircleShape,
        modifier = modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(warna, CircleShape)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = warna,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StateKosongQControl(
    ikon: String,
    judul: String,
    pesan: String,
    modifier: Modifier = Modifier,
    onAksi: (() -> Unit)? = null,
    labelAksi: String = ""
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiSedang),
            modifier = Modifier.widthIn(max = 400.dp).padding(UkuranQControl.SpasiNormal)
        ) {
            Text(ikon, style = MaterialTheme.typography.displayMedium)
            Text(
                text = judul,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = pesan,
                style = MaterialTheme.typography.bodySmall,
                color = TeksAbuAbu,
                textAlign = TextAlign.Center
            )
            if (onAksi != null && labelAksi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                Button(
                    onClick = onAksi,
                    colors = ButtonDefaults.buttonColors(containerColor = SolarYellow, contentColor = TeksGelap),
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal)
                ) {
                    Text(labelAksi, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PembatasHalusQControl(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = GarisHalus
    )
}
