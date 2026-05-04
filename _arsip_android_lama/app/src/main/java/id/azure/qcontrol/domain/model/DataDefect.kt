package id.azure.qcontrol.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DataDefect(
    val id: String,
    val jenisDefect: String,
    val jumlah: Int,
    val area: String,
    val inspeksiId: String
)
