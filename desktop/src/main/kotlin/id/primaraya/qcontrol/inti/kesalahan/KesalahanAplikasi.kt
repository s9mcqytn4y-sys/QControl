package id.primaraya.qcontrol.inti.kesalahan

sealed class KesalahanAplikasi(val pesan: String) {
    class KoneksiServer(pesan: String) : KesalahanAplikasi(pesan)
    class ResponTidakValid(pesan: String) : KesalahanAplikasi(pesan)
    class Server(pesan: String, val kode: String? = null) : KesalahanAplikasi(pesan)
    class TidakDiketahui(pesan: String) : KesalahanAplikasi(pesan)
    class PenyimpananLokal(pesan: String) : KesalahanAplikasi(pesan)
    class DataKosong(pesan: String) : KesalahanAplikasi(pesan)
    class DataTidakDitemukan(pesan: String) : KesalahanAplikasi(pesan)
    class Validasi(pesan: String) : KesalahanAplikasi(pesan)
}
