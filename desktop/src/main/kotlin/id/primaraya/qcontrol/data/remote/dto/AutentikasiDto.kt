package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermintaanLoginDto(
    @SerialName("email") val email: String,
    @SerialName("password") val kataSandi: String
)

@Serializable
data class ResponAutentikasiDto(
    val token: String,
    val profil: ProfilPenggunaDto
)

@Serializable
data class ProfilPenggunaDto(
    @SerialName("namaPengguna") val namaPengguna: String,
    val peran: String,
    val email: String? = null
)
