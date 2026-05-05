package id.primaraya.qcontrol.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkemaWarnaTerang = lightColorScheme(
    primary = DeepAmber,
    onPrimary = Color.White,
    primaryContainer = SolarYellow,
    onPrimaryContainer = TeksGelap,
    secondary = VibrantOrange,
    onSecondary = Color.White,
    background = LatarBelakangKonten,
    surface = Color.White,
    onSurface = TeksGelap,
    surfaceVariant = LatarBelakangSidebar,
    onSurfaceVariant = TeksTerang
)

@Composable
fun TemaQControl(
    temaGelap: Boolean = isSystemInDarkTheme(),
    konten: @Composable () -> Unit
) {
    val skemaWarna = if (temaGelap) {
        darkColorScheme(
            primary = SolarYellow,
            onPrimary = TeksGelap,
            primaryContainer = DeepAmber,
            onPrimaryContainer = Color.White,
            secondary = VibrantOrange,
            onSecondary = Color.White,
            tertiary = DeepAmber,
            background = LatarBelakangGelap,
            onBackground = TeksTerang,
            surface = LatarBelakangSidebar,
            onSurface = TeksTerang,
            error = GagalMerah
        )
    } else {
        SkemaWarnaTerang
    }

    MaterialTheme(
        colorScheme = skemaWarna,
        typography = TipografiQControl,
        content = konten
    )
}
