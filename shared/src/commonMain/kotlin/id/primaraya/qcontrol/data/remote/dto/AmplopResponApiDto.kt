package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AmplopResponApiDto<T>(
    val berhasil: Boolean,
    val pesan: String,
    val data: T? = null,
    val metadata: kotlinx.serialization.json.JsonObject? = null,
    val kesalahan: KesalahanApiDto? = null
)

@Serializable
data class KesalahanApiDto(
    val kode: String,
    val detail: List<DetailKesalahanApiDto> = emptyList()
)

@Serializable
data class DetailKesalahanApiDto(
    val field: String? = null,
    val pesan: String
)
