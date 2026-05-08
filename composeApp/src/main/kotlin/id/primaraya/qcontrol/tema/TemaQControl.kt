package id.primaraya.qcontrol.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Tema Premium QControl - Material 3 Hardening
 */

private val SkemaWarnaGelapPremium = darkColorScheme(
    primary = SolarYellow,
    onPrimary = LatarBelakangUtama,
    primaryContainer = DeepAmber,
    onPrimaryContainer = TeksKontrasTinggi,
    secondary = VibrantOrange,
    onSecondary = Color.White,
    tertiary = InfoBiru,
    onTertiary = LatarBelakangUtama,
    background = LatarBelakangUtama,
    onBackground = TeksKontrasTinggi,
    surface = LatarBelakangSidebar,
    onSurface = TeksKontrasTinggi,
    surfaceVariant = LatarBelakangPanel,
    onSurfaceVariant = TeksKontrasSedang,
    outline = GarisHalus,
    error = GagalMerah,
    onError = Color.White,
    errorContainer = GagalMerah.copy(alpha = 0.2f),
    onErrorContainer = GagalMerah
)

// Tetap sediakan skema terang namun prioritaskan visual gelap untuk QC
private val SkemaWarnaTerangStandard = lightColorScheme(
    primary = DeepAmber,
    onPrimary = Color.White,
    primaryContainer = SolarYellow,
    onPrimaryContainer = LatarBelakangUtama,
    secondary = VibrantOrange,
    onSecondary = Color.White,
    background = Color(0xFFF1F5F9), // Slate 100
    surface = Color.White,
    onSurface = LatarBelakangUtama,
    surfaceVariant = Color(0xFFE2E8F0), // Slate 200
    onSurfaceVariant = TeksKontrasRendah,
    outline = Color(0xFFCBD5E1) // Slate 300
)

@Composable
fun TemaQControl(
    temaGelap: Boolean = true, // Default dipaksa gelap untuk kesan premium manufacturing
    konten: @Composable () -> Unit
) {
    val skemaWarna = if (temaGelap) SkemaWarnaGelapPremium else SkemaWarnaTerangStandard

    MaterialTheme(
        colorScheme = skemaWarna,
        typography = TipografiQControl,
        shapes = BentukQControl,
        content = konten
    )
}
