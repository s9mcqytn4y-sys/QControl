package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PemeriksaanHarianDto(
    val id: String,
    val clientDraftId: String?,
    val tanggalProduksi: String,
    val lineProduksiId: String,
    val nomorDokumen: String?,
    val revisi: String?,
    val catatan: String?,
    val createdBy: String?,
    val daftarPart: List<PemeriksaanPartDto> = emptyList()
)

@Serializable
data class PemeriksaanPartDto(
    val id: String,
    val pemeriksaanHarianId: String,
    val partId: String,
    val totalCheck: Int,
    val totalOk: Int,
    val totalDefect: Int,
    val rasioDefect: Double,
    val daftarDefect: List<PemeriksaanDefectSlotDto> = emptyList()
)

@Serializable
data class PemeriksaanDefectSlotDto(
    val id: String,
    val pemeriksaanPartId: String,
    val relasiPartDefectId: String,
    val slotWaktuId: String,
    val jumlahDefect: Int
)
