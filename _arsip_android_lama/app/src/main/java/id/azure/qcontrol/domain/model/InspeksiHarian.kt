package id.azure.qcontrol.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InspeksiHarian(
    val id: String,
    val totalCheck: Int,
    val totalDefect: Int,
    val rasioNg: Float,
    val timestamp: Long
)
