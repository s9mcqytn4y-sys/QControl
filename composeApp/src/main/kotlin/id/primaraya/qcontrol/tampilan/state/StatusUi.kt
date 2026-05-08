package id.primaraya.qcontrol.tampilan.state

enum class TipePesanFlash {
    SUKSES,
    ERROR,
    PERINGATAN,
    INFO
}

sealed class StatusKoneksiServer {
    data object TidakDiperiksa : StatusKoneksiServer()
    data object Memeriksa : StatusKoneksiServer()
    data object Tersambung : StatusKoneksiServer()
    data object Terputus : StatusKoneksiServer()
}
