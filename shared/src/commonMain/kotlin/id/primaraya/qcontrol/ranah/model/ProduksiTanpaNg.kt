package id.primaraya.qcontrol.ranah.model

import kotlinx.serialization.Serializable

@Serializable
data class ProduksiTanpaNg(
    val id: String,
    val partId: String,
    val uniqNoPart: String,
    val nomorPartSnapshot: String,
    val namaPartSnapshot: String,
    val totalProduksi: Int,
    val catatan: String? = null
)

data class DraftProduksiTanpaNg(
    val partId: String,
    val nomorPart: String,
    val namaPart: String,
    val totalProduksi: Int,
    val catatan: String? = null
)
