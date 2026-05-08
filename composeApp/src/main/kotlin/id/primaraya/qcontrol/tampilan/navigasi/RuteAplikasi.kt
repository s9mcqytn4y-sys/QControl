package id.primaraya.qcontrol.tampilan.navigasi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector

sealed class RuteAplikasi(
    val rute: String,
    val judul: String,
    val labelMenu: String,
    val ikon: ImageVector,
    val deskripsi: String,
    val urutan: Int
) {
    data object Dashboard : RuteAplikasi(
        rute = "dashboard",
        judul = "DASHBOARD",
        labelMenu = "Dashboard",
        ikon = Icons.Default.Dashboard,
        deskripsi = "Monitoring ringkasan inspeksi hari ini",
        urutan = 1
    )

    data object InputHarian : RuteAplikasi(
        rute = "input_harian",
        judul = "INPUT HARIAN",
        labelMenu = "Input Harian",
        ikon = Icons.Default.EditNote,
        deskripsi = "Pencatatan hasil inspeksi harian",
        urutan = 2
    )

    data object MasterData : RuteAplikasi(
        rute = "master_data",
        judul = "DATA ACUAN",
        labelMenu = "Data Acuan",
        ikon = Icons.Default.Storage,
        deskripsi = "Data referensi inspeksi dari server perusahaan",
        urutan = 3
    )

    companion object {
        fun dapatkanDaftarRute() = listOf(
            Dashboard,
            InputHarian,
            MasterData
        )
    }
}
