package id.primaraya.qcontrol.utilitas

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object FormatWaktu {
    private val formatterIndo = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))

    fun formatTanggalIndo(tanggalIso: String): String {
        return try {
            val date = LocalDate.parse(tanggalIso)
            date.format(formatterIndo)
        } catch (e: Exception) {
            tanggalIso
        }
    }
}
