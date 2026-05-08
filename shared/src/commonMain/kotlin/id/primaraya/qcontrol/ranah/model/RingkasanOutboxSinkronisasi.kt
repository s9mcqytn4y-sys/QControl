package id.primaraya.qcontrol.ranah.model

data class RingkasanOutboxSinkronisasi(
    val jumlahMenunggu: Int,
    val jumlahSedangDikirim: Int,
    val jumlahBerhasil: Int,
    val jumlahGagal: Int,
    val jumlahKonflik: Int,
    val jumlahTotal: Int
)
