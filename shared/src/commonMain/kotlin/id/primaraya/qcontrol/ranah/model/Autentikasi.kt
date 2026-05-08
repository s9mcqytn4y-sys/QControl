package id.primaraya.qcontrol.ranah.model

import id.primaraya.qcontrol.konfigurasi.KonfigurasiPeran

/**
 * Model data yang merepresentasikan sesi aktif pengguna.
 */
data class Autentikasi(
    val token: String,
    val namaPengguna: String,
    val peran: String,
    val email: String? = null
) {
    fun adalahHeadQC(): Boolean = peran == KonfigurasiPeran.HEAD_QC
}
