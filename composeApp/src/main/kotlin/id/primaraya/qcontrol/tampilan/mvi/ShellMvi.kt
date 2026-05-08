package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.mvi.MviEffect
import id.primaraya.qcontrol.inti.mvi.MviIntent
import id.primaraya.qcontrol.inti.mvi.MviState
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tampilan.state.TipePesanFlash

sealed class ShellIntent : MviIntent {
    data class PilihRute(val rute: RuteAplikasi) : ShellIntent()
    object PeriksaKoneksi : ShellIntent()
    data class TampilkanPesan(val pesan: String, val tipe: TipePesanFlash) : ShellIntent()
    object BersihkanPesan : ShellIntent()
}

data class ShellState(
    val ruteAktif: RuteAplikasi = RuteAplikasi.Dashboard,
    val statusKoneksi: StatusKoneksiServer = StatusKoneksiServer.TidakDiperiksa,
    val pesanStatusKoneksi: String = "",
    val pesanFlash: ShellPesanFlash? = null
) : MviState

data class ShellPesanFlash(val pesan: String, val tipe: TipePesanFlash)

sealed class ShellEffect : MviEffect
