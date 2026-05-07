package id.primaraya.qcontrol.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PermintaanSimpanPemeriksaanHarianDto(
    val clientDraftId: String,
    val tanggalProduksi: String,
    val lineProduksiId: String,
    val nomorDokumen: String?,
    val revisi: String?,
    val catatan: String?,
    val daftarPart: List<PermintaanSimpanPemeriksaanPartDto>
)

@Serializable
data class PermintaanSimpanPemeriksaanPartDto(
    val partId: String,
    val totalCheck: Int,
    val daftarDefect: List<PermintaanSimpanPemeriksaanDefectDto>
)

@Serializable
data class PermintaanSimpanPemeriksaanDefectDto(
    val relasiPartDefectId: String,
    val slotWaktuId: String,
    val jumlahDefect: Int
)
