package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartDto(
    val id: String,
    val kodeUnikPart: String,
    val namaPart: String,
    val nomorPart: String? = null,
    val materialId: String? = null,
    val kodeMaterial: String? = null,
    val namaMaterial: String? = null,
    val kodeProyek: String? = null,
    val jumlahItemPerKanban: Int? = null,
    val lineDefaultId: String? = null,
    val kodeLineDefault: String? = null,
    val namaLineDefault: String? = null,
    val aktif: Boolean,
    val sumberData: String? = null
)
