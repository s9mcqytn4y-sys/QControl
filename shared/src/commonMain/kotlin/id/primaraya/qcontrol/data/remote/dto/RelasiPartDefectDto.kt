package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RelasiPartDefectDto(
    val id: String,
    val partId: String,
    val kodeUnikPart: String? = null,
    val jenisDefectId: String,
    val kodeDefect: String? = null,
    val kodeTampilanDefect: String? = null,
    val urutanTampil: Int,
    val aktif: Boolean
)
