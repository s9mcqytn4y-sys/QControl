package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermintaanLoginDto(
    @SerialName("nama_pengguna") val namaPengguna: String,
    @SerialName("kata_sandi") val kataSandi: String
)

@Serializable
data class ResponAutentikasiDto(
    val token: String,
    val profil: ProfilPenggunaDto
)

@Serializable
data class ProfilPenggunaDto(
    @SerialName("nama_pengguna") val namaPengguna: String,
    val peran: String
)
