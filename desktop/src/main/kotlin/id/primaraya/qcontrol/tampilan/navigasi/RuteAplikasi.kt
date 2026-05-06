package id.primaraya.qcontrol.tampilan.navigasi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class RuteAplikasi(
    val rute: String,
    val judul: String,
    val labelMenu: String,
    val ikon: ImageVector,
    val deskripsi: String,
    val urutan: Int
) {
    object Dashboard : RuteAplikasi(
        rute = "dashboard",
        judul = "DASHBOARD",
        labelMenu = "Dashboard",
        ikon = Icons.Default.Dashboard,
        deskripsi = "Monitoring ringkasan data defect produksi",
        urutan = 1
    )

    object InputHarian : RuteAplikasi(
        rute = "input_harian",
        judul = "INPUT HARIAN",
        labelMenu = "Input Harian",
        ikon = Icons.Default.EditNote,
        deskripsi = "Pencatatan hasil inspeksi harian",
        urutan = 2
    )

    object RecordingDataDefect : RuteAplikasi(
        rute = "recording_defect",
        judul = "RECORDING DATA DEFECT",
        labelMenu = "Recording Data Defect",
        ikon = Icons.Default.HistoryEdu,
        deskripsi = "Rekaman detail temuan defect produksi",
        urutan = 3
    )

    object ControlChart : RuteAplikasi(
        rute = "control_chart",
        judul = "CONTROL CHART",
        labelMenu = "Control Chart",
        ikon = Icons.AutoMirrored.Filled.ShowChart,
        deskripsi = "Analisis statistik proses produksi",
        urutan = 4
    )

    object LaporanBulanan : RuteAplikasi(
        rute = "laporan_bulanan",
        judul = "LAPORAN BULANAN",
        labelMenu = "Laporan Bulanan",
        ikon = Icons.Default.Assessment,
        deskripsi = "Rekapitulasi data kualitas bulanan",
        urutan = 5
    )

    object MasterData : RuteAplikasi(
        rute = "master_data",
        judul = "MASTER DATA",
        labelMenu = "Master Data",
        ikon = Icons.Default.Storage,
        deskripsi = "Pengaturan data referensi sistem",
        urutan = 6
    )

    object Pengaturan : RuteAplikasi(
        rute = "pengaturan",
        judul = "PENGATURAN",
        labelMenu = "Pengaturan",
        ikon = Icons.Default.Settings,
        deskripsi = "Konfigurasi aplikasi dan profil",
        urutan = 7
    )

    companion object {
        fun dapatkanDaftarRute() = listOf(
            Dashboard,
            InputHarian,
            RecordingDataDefect,
            ControlChart,
            LaporanBulanan,
            MasterData,
            Pengaturan
        ).sortedBy { it.urutan }
    }
}
