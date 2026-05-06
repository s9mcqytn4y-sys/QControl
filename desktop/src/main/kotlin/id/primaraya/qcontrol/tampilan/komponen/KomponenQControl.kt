package id.primaraya.qcontrol.tampilan.komponen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tema.*

/**
 * Komponen UI Premium QControl - Material 3 Hardening
 */

@Composable
fun PanelPremiumQControl(
    modifier: Modifier = Modifier,
    judul: String? = null,
    konten: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(UkuranQControl.SpasiNormal)) {
            if (judul != null) {
                Text(
                    text = judul.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TeksKontrasRendah,
                    modifier = Modifier.padding(bottom = UkuranQControl.SpasiNormal)
                )
            }
            konten()
        }
    }
}

@Composable
fun TombolUtamaQControl(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    ikon: ImageVector? = null,
    sedangMemuat: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !sedangMemuat,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = LatarBelakangUtama
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (sedangMemuat) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = LatarBelakangUtama,
                strokeWidth = 2.dp
            )
        } else {
            if (ikon != null) {
                Icon(ikon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TombolSekunderQControl(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    ikon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (ikon != null) {
            Icon(ikon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FieldInputQControl(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) MaterialTheme.colorScheme.error else TeksKontrasSedang,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { { Text(it, color = TeksKontrasRendah) } },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = TeksKontrasRendah) } },
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            enabled = enabled,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = GarisHalus,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = LatarBelakangUtama.copy(alpha = 0.5f),
                unfocusedContainerColor = LatarBelakangUtama.copy(alpha = 0.3f)
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun ChipStatusQControl(
    label: String,
    warna: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = warna.copy(alpha = 0.15f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, warna.copy(alpha = 0.3f)),
        modifier = modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(warna, CircleShape)
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = warna,
                fontWeight = FontWeight.Black
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
            modifier = Modifier.widthIn(max = 450.dp).padding(UkuranQControl.SpasiNormal)
        ) {
            Text(ikon, style = MaterialTheme.typography.displayLarge)
            Text(
                text = judul,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = TeksKontrasTinggi
            )
            Text(
                text = pesan,
                style = MaterialTheme.typography.bodyMedium,
                color = TeksKontrasSedang,
                textAlign = TextAlign.Center
            )
            if (onAksi != null && labelAksi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
                TombolUtamaQControl(
                    text = labelAksi,
                    onClick = onAksi
                )
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

@Composable
fun HeaderHalamanQControl(
    judul: String,
    subtitle: String,
    aksi: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = UkuranQControl.SpasiBesar),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = judul,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TeksKontrasTinggi
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TeksKontrasRendah
            )
        }
        if (aksi != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                aksi()
            }
        }
    }
}

@Composable
fun KartuInfoPemeriksaan(
    label: String,
    nilai: String,
    warna: Color,
    modifier: Modifier = Modifier
) {
    PanelPremiumQControl(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TeksKontrasRendah
            )
            Text(
                text = nilai,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = warna
            )
        }
    }
}
