package id.primaraya.qcontrol

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import id.primaraya.qcontrol.di.initKoin
import id.primaraya.qcontrol.di.tampilanModule
import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.tampilan.AplikasiQControl

fun main() {
    initKoin {
        modules(tampilanModule)
    }
    application {
        val stateJendela = rememberWindowState(
            width = 1366.dp, // Sedikit lebih lebar untuk standar pabrik
            height = 768.dp
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = stateJendela,
            title = KonfigurasiAplikasi.JUDUL_WINDOW,
            icon = painterResource("logo_qcontrol.png")
        ) {
            AplikasiQControl()
        }
    }
}
