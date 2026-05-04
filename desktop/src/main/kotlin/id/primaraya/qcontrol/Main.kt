package id.primaraya.qcontrol

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import id.primaraya.qcontrol.tampilan.AplikasiQControl

fun main() = application {
    val stateJendela = rememberWindowState(
        width = 1200.dp,
        height = 800.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = stateJendela,
        title = "QControl - Sistem Manajemen Kualitas"
    ) {
        AplikasiQControl()
    }
}
