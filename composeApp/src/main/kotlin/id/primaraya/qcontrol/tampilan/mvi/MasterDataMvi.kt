package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.mvi.MviEffect
import id.primaraya.qcontrol.inti.mvi.MviIntent
import id.primaraya.qcontrol.inti.mvi.MviState
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.state.TabMasterData

sealed class MasterDataIntent : MviIntent {
    object TarikDariServer : MasterDataIntent()
    object MuatLokal : MasterDataIntent()
    data class PilihTab(val tab: TabMasterData) : MasterDataIntent()
    data class Cari(val kataKunci: String) : MasterDataIntent()
}

data class MasterDataState(
    val sedangMemuat: Boolean = false,
    val pesan: String? = null,
    val ringkasan: RingkasanMasterData? = null,
    val daftarPart: List<Part> = emptyList(),
    val daftarJenisDefect: List<JenisDefect> = emptyList(),
    val daftarMaterial: List<Material> = emptyList(),
    val daftarSlotWaktu: List<SlotWaktu> = emptyList(),
    val daftarLineProduksi: List<LineProduksi> = emptyList(),
    val tabAktif: TabMasterData = TabMasterData.RINGKASAN,
    val kataKunci: String = ""
) : MviState

sealed class MasterDataEffect : MviEffect {
    data class TampilkanPesan(val pesan: String, val sukses: Boolean) : MasterDataEffect()
}
