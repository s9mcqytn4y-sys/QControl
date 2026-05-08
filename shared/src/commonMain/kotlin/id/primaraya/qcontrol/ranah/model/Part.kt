package id.primaraya.qcontrol.ranah.model

data class Part(
    val id: String,
    val kodeUnikPart: String,
    val namaPart: String,
    val nomorPart: String? = null,
    val materialId: String? = null,
    val kodeMaterial: String? = null,
    val namaMaterial: String? = null,
    val kodeProyek: String? = null,
    val jumlahItemPerKanban: Int? = null,
    val lineDefaultId: String? = null,
    val kodeLineDefault: String? = null,
    val namaLineDefault: String? = null,
    val aktif: Boolean,
    val sumberData: String? = null
)
