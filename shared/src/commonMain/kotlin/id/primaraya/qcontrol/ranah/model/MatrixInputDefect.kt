package id.primaraya.qcontrol.ranah.model

/**
 * Representasi matrix defect x slot waktu untuk satu part.
 */
data class MatrixInputDefectPart(
    val partId: String,
    val namaPart: String,
    val nomorPart: String,
    val barisDefect: List<BarisInputDefect>,
    val kolomSlotWaktu: List<KolomSlotWaktuInput>,
    val ringkasan: RingkasanSlotWaktuInput
)

/**
 * Baris dalam matrix yang mewakili satu jenis defect dari template.
 */
data class BarisInputDefect(
    val relasiPartDefectId: String,
    val namaDefect: String,
    val kodeDefect: String,
    val nilaiPerSlot: List<NilaiInputDefectSlot>, // Nilai di setiap kolom slot waktu
    val subtotal: Int
)

/**
 * Definisi kolom slot waktu yang aktif.
 */
data class KolomSlotWaktuInput(
    val slotWaktuId: String,
    val labelSlot: String,
    val kodeSlot: String,
    val urutan: Int
)

/**
 * Nilai jumlah defect pada sel spesifik (Defect X, Slot Y).
 */
data class NilaiInputDefectSlot(
    val slotWaktuId: String,
    val jumlahDefect: Int
)

/**
 * Ringkasan statistik per slot waktu (agregasi kolom).
 */
data class RingkasanSlotWaktuInput(
    val totalPerSlot: Map<String, Int>, // slotWaktuId -> total defect
    val totalDefectPart: Int,
    val totalOkPart: Int,
    val rasioDefect: Double
)
