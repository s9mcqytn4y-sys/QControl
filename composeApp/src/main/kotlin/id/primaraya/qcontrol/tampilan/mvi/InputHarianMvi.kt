package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.mvi.MviEffect
import id.primaraya.qcontrol.inti.mvi.MviIntent
import id.primaraya.qcontrol.inti.mvi.MviState
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.state.TipePesanFlash

sealed class InputHarianIntent : MviIntent {
    data class Inisialisasi(val tanggal: String, val lineId: String) : InputHarianIntent()
    data class GantiLine(val lineId: String) : InputHarianIntent()
    data class GantiTanggal(val tanggal: String) : InputHarianIntent()
    data class CariPart(val kataKunci: String) : InputHarianIntent()
    data class PilihPart(val part: DraftInputPart?) : InputHarianIntent()
    data class UpdateQtyCheck(val partId: String, val qty: Int) : InputHarianIntent()
    data class UpdateDefect(val partId: String, val slotId: String, val relasiId: String, val qty: Int) : InputHarianIntent()
    data class UpdateProduksiTanpaNg(val partId: String, val qty: Int) : InputHarianIntent()
    object KirimKeServer : InputHarianIntent()
    object ResetDraft : InputHarianIntent()
}

data class InputHarianState(
    val sedangMemuat: Boolean = false,
    val draft: DraftPemeriksaanHarian? = null,
    val daftarPart: List<DraftInputPart> = emptyList(),
    val daftarProduksiTanpaNg: List<DraftProduksiTanpaNg> = emptyList(),
    val partTerpilih: DraftInputPart? = null,
    val matrixDefect: MatrixInputDefectPart? = null,
    val ringkasan: RingkasanInputHarian? = null,
    val kataKunciPart: String = "",
    val tanggal: String = "",
    val lineId: String = "",
    val pesanValidasi: String? = null
) : MviState

sealed class InputHarianEffect : MviEffect {
    data class TampilkanPesan(val pesan: String, val tipe: TipePesanFlash) : InputHarianEffect()
}
