package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MasterDataQControlDto(
    val versiMasterData: String,
    val lineProduksi: List<LineProduksiDto> = emptyList(),
    val slotWaktu: List<SlotWaktuDto> = emptyList(),
    val material: List<MaterialDto> = emptyList(),
    val part: List<PartDto> = emptyList(),
    val kategoriDefect: List<KategoriDefectDto> = emptyList(),
    val jenisDefect: List<JenisDefectDto> = emptyList(),
    val relasiPartDefect: List<RelasiPartDefectDto> = emptyList()
)
