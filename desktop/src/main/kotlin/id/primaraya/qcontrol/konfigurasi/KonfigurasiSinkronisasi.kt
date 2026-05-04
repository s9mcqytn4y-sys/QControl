package id.primaraya.qcontrol.konfigurasi

/**
 * Konfigurasi untuk sistem sinkronisasi data outbox.
 */
object KonfigurasiSinkronisasi {
    /**
     * Apakah sinkronisasi otomatis aktif secara default saat aplikasi dibuka.
     * Untuk fase fondasi, diatur ke false agar tidak membebani server/log jika endpoint belum ada.
     */
    const val SINKRONISASI_OTOMATIS_AKTIF_DEFAULT = false

    /**
     * Interval waktu antar siklus sinkronisasi otomatis.
     */
    const val INTERVAL_SINKRONISASI_MILIDETIK = 30000L

    /**
     * Jumlah maksimal item outbox yang diproses dalam satu siklus sinkronisasi.
     */
    const val BATAS_ITEM_PER_SIKLUS = 10

    /**
     * Batas maksimal percobaan pengiriman item outbox sebelum ditandai gagal permanen (opsional).
     */
    const val BATAS_MAKSIMAL_PERCOBAAN = 3

    /**
     * Batas waktu (menit) untuk menganggap item "SEDANG_DIKIRIM" telah kadaluarsa/stuck.
     */
    const val BATAS_MENIT_RESET_SEDANG_DIKIRIM = 10L
}
