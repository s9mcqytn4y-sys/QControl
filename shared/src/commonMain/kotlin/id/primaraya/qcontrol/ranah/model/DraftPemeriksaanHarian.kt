package id.primaraya.qcontrol.ranah.model

data class DraftPemeriksaanHarian(
    val id: String,
    val clientDraftId: String,
    val tanggalProduksi: String,
    val lineId: String,
    val nomorDokumen: String? = null,
    val revisi: String? = null,
    val catatan: String? = null,
    val statusDraft: String = "DRAFT",
    val idempotencyKey: String,
    val hashPayload: String? = null,
    val terakhirDisimpanPada: String,
    val terakhirDikirimPada: String? = null,
    val pesanErrorTerakhir: String? = null,
    val dibuatPada: String,
    val diperbaruiPada: String
)
