package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SlotWaktuDto(
    val id: String,
    val kodeSlot: String,
    val labelSlot: String,
    val jamMulai: String? = null,
    val jamSelesai: String? = null,
    val aktif: Boolean,
    val urutanTampil: Int
)
