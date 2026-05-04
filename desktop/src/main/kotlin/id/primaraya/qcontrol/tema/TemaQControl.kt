package id.primaraya.qcontrol.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SolarYellow = Color(0xFFFFD700)
val VibrantOrange = Color(0xFFFF8C00)
val DeepAmber = Color(0xFFFF4500)

private val SkemaWarnaTerang = lightColorScheme(
    primary = DeepAmber,
    onPrimary = Color.White,
    primaryContainer = SolarYellow,
    onPrimaryContainer = Color.Black,
    secondary = VibrantOrange,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun TemaQControl(
    temaGelap: Boolean = isSystemInDarkTheme(),
    konten: @Composable () -> Unit
) {
    val skemaWarna = if (temaGelap) {
        // Sementara pakai skema terang dulu atau sesuaikan nanti
        darkColorScheme(
            primary = SolarYellow,
            secondary = VibrantOrange,
            tertiary = DeepAmber
        )
    } else {
        SkemaWarnaTerang
    }

    MaterialTheme(
        colorScheme = skemaWarna,
        typography = Typography(),
        content = konten
    )
}
