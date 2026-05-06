# Compose Assets - QControl

Aset ini siap ditempatkan ke folder `app/src/main/res/` pada project Android Studio.

## Nama aplikasi
QControl

## Isi utama
- `res/drawable/logo_qc.xml` - logo utama berbasis Vector Drawable.
- `res/drawable/logo_qc_hd.png` - logo PNG HD 1024x1024 transparan.
- `res/drawable/logo_qc_hd_webp.webp` - alternatif WebP.
- `res/drawable/ic_launcher_foreground.xml` - foreground adaptive icon.
- `res/drawable/ic_launcher_background.xml` - background adaptive icon.
- `res/mipmap-anydpi-v26/ic_launcher.xml` - adaptive launcher icon.
- `res/mipmap-anydpi-v26/ic_launcher_round.xml` - adaptive round launcher icon.
- `res/mipmap-*/ic_launcher.png` - launcher icon legacy per density.
- `res/mipmap-*/ic_launcher_round.png` - round launcher icon legacy per density.
- `res/drawable/ic_home.xml`
- `res/drawable/ic_search.xml`
- `res/drawable/ic_profile.xml`
- `res/drawable/ic_settings.xml`

## Contoh penggunaan di Jetpack Compose

```kotlin
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier

@Composable
fun LogoQControl(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_qc),
        contentDescription = "Logo QControl",
        modifier = modifier
    )
}
```

## Catatan
- `VectorDrawable` cocok untuk ikon UI dan logo yang perlu scalable.
- PNG launcher icon disediakan untuk kompatibilitas density Android.
- Adaptive icon memakai foreground/background sesuai struktur Android modern.
