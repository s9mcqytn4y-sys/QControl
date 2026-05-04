package id.primaraya.qcontrol.ranah.model

data class StatusKesehatanServer(
    val status: String,
    val namaAplikasi: String,
    val versiApi: String,
    val waktuServer: String,
    val zonaWaktu: String,
    val koneksiDatabase: StatusKoneksiDatabase
)

data class StatusKoneksiDatabase(
    val status: String,
    val driver: String
)
