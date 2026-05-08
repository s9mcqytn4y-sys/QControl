package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JenisDefectDto(
    val id: String,
    val kodeDefect: String,
    val namaDefect: String,
    val kategoriDefectId: String? = null,
    val kodeKategori: String? = null,
    val namaKategori: String? = null,
    val aktif: Boolean
)
