package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MetadataMasterDataDto(
    val jumlahLineProduksi: Int,
    val jumlahSlotWaktu: Int,
    val jumlahMaterial: Int,
    val jumlahPart: Int,
    val jumlahJenisDefect: Int,
    val jumlahRelasiPartDefect: Int,
    val jumlahShiftOperasional: Int
)
