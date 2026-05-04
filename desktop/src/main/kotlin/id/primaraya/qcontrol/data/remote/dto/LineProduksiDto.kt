package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LineProduksiDto(
    val id: String,
    val kodeLine: String,
    val namaLine: String,
    val aktif: Boolean,
    val urutanTampil: Int
)
