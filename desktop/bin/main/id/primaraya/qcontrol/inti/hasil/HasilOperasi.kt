package id.primaraya.qcontrol.inti.hasil

import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi

sealed class HasilOperasi<out T> {
    data class Berhasil<T>(val data: T) : HasilOperasi<T>()
    data class Gagal(val kesalahan: KesalahanAplikasi) : HasilOperasi<Nothing>()
}
