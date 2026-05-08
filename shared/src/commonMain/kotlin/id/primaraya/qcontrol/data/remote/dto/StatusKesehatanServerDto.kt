package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatusKesehatanServerDto(
    val status: String,
    val namaAplikasi: String,
    val versiApi: String,
    val waktuServer: String,
    val zonaWaktu: String,
    val koneksiDatabase: KoneksiDatabaseDto
)

@Serializable
data class KoneksiDatabaseDto(
    val status: String,
    val driver: String
)
