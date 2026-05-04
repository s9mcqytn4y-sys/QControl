package id.primaraya.qcontrol.data.lokal.database

data class StatusDatabaseLokal(
    val tersedia: Boolean,
    val pathDatabase: String,
    val versiMigrasiTerakhir: Int?,
    val jumlahItemOutboxMenunggu: Int,
    val pesan: String
)
