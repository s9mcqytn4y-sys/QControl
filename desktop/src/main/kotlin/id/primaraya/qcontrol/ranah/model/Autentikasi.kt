package id.primaraya.qcontrol.ranah.model

/**
 * Model data yang merepresentasikan sesi aktif pengguna.
 */
data class Autentikasi(
    val token: String,
    val namaPengguna: String,
    val peran: String
)
