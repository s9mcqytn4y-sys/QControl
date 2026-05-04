package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MaterialDto(
    val id: String,
    val kodeMaterial: String? = null,
    val namaMaterial: String,
    val aktif: Boolean
)
