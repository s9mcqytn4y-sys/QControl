package id.primaraya.qcontrol.data.lokal.model

data class StatusServerLokalEntity(
    val id: Int = 1,
    val status: String,
    val namaAplikasi: String?,
    val versiApi: String?,
    val waktuServer: String?,
    val zonaWaktu: String?,
    val statusDatabase: String?,
    val driverDatabase: String?,
    val diperbaruiPada: String
)
