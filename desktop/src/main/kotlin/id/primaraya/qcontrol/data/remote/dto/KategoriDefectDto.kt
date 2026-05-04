package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class KategoriDefectDto(
    val id: String,
    val kodeKategori: String,
    val namaKategori: String,
    val aktif: Boolean,
    val urutanTampil: Int
)
