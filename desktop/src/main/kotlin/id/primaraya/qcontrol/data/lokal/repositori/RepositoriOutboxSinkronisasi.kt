package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.MigrasiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.pemetaan.keItemOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi
import id.primaraya.qcontrol.ranah.model.StatusOutboxSinkronisasi
import java.sql.Connection

class RepositoriOutboxSinkronisasi(
    private val koneksi: KoneksiDatabaseLokal,
    private val migrasi: MigrasiDatabaseLokal
) {
    fun tambah(item: ItemOutboxSinkronisasi): HasilOperasi<Unit> {
        return try {
            migrasi.jalankanMigrasi()
            koneksi.bukaKoneksi().use { conn ->
                val sql = """
                    INSERT INTO outbox_sinkronisasi (
                        id, jenis_operasi, endpoint_tujuan, metode_http, payload_json, 
                        idempotency_key, status, jumlah_percobaan, pesan_gagal_terakhir, 
                        dibuat_pada, diperbarui_pada
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
                
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, item.id)
                    ps.setString(2, item.jenisOperasi)
                    ps.setString(3, item.endpointTujuan)
                    ps.setString(4, item.metodeHttp.name)
                    ps.setString(5, item.payloadJson)
                    ps.setString(6, item.idempotencyKey)
                    ps.setString(7, item.status.name)
                    ps.setInt(8, item.jumlahPercobaan)
                    ps.setString(9, item.pesanGagalTerakhir)
                    ps.setString(10, item.dibuatPada)
                    ps.setString(11, item.diperbaruiPada)
                    ps.executeUpdate()
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menambah item outbox: ${e.message}"))
        }
    }

    fun bacaMenunggu(batas: Int = 50): HasilOperasi<List<ItemOutboxSinkronisasi>> {
        return try {
            migrasi.jalankanMigrasi()
            val daftar = mutableListOf<ItemOutboxSinkronisasi>()
            koneksi.bukaKoneksi().use { conn ->
                val sql = "SELECT * FROM outbox_sinkronisasi WHERE status = ? ORDER BY dibuat_pada ASC LIMIT ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, StatusOutboxSinkronisasi.MENUNGGU.name)
                    ps.setInt(2, batas)
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            daftar.add(rs.keItemOutboxSinkronisasi())
                        }
                    }
                }
            }
            HasilOperasi.Berhasil(daftar)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca outbox menunggu: ${e.message}"))
        }
    }

    fun bacaRingkasan(): HasilOperasi<RingkasanOutboxSinkronisasi> {
        return try {
            migrasi.jalankanMigrasi()
            koneksi.bukaKoneksi().use { conn ->
                val sql = """
                    SELECT 
                        COUNT(CASE WHEN status = 'MENUNGGU' THEN 1 END) as menunggu,
                        COUNT(CASE WHEN status = 'SEDANG_DIKIRIM' THEN 1 END) as sedang_dikirim,
                        COUNT(CASE WHEN status = 'BERHASIL' THEN 1 END) as berhasil,
                        COUNT(CASE WHEN status = 'GAGAL' THEN 1 END) as gagal,
                        COUNT(CASE WHEN status = 'KONFLIK' THEN 1 END) as konflik,
                        COUNT(*) as total
                    FROM outbox_sinkronisasi
                """.trimIndent()
                
                conn.createStatement().use { stmt ->
                    stmt.executeQuery(sql).use { rs ->
                        if (rs.next()) {
                            val ringkasan = RingkasanOutboxSinkronisasi(
                                jumlahMenunggu = rs.getInt("menunggu"),
                                jumlahSedangDikirim = rs.getInt("sedang_dikirim"),
                                jumlahBerhasil = rs.getInt("berhasil"),
                                jumlahGagal = rs.getInt("gagal"),
                                jumlahKonflik = rs.getInt("konflik"),
                                jumlahTotal = rs.getInt("total")
                            )
                            HasilOperasi.Berhasil(ringkasan)
                        } else {
                            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Data ringkasan tidak ditemukan"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca ringkasan outbox: ${e.message}"))
        }
    }

    fun tandaiSedangDikirim(id: String): HasilOperasi<Unit> {
        return perbaruiStatus(id, StatusOutboxSinkronisasi.SEDANG_DIKIRIM)
    }

    fun tandaiBerhasil(id: String): HasilOperasi<Unit> {
        return perbaruiStatus(id, StatusOutboxSinkronisasi.BERHASIL)
    }

    fun tandaiGagal(id: String, pesan: String): HasilOperasi<Unit> {
        return try {
            koneksi.bukaKoneksi().use { conn ->
                val sql = "UPDATE outbox_sinkronisasi SET status = ?, pesan_gagal_terakhir = ?, jumlah_percobaan = jumlah_percobaan + 1, diperbarui_pada = CURRENT_TIMESTAMP WHERE id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, StatusOutboxSinkronisasi.GAGAL.name)
                    ps.setString(2, pesan)
                    ps.setString(3, id)
                    ps.executeUpdate()
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menandai outbox gagal: ${e.message}"))
        }
    }

    fun tandaiKonflik(id: String, pesan: String): HasilOperasi<Unit> {
        return try {
            koneksi.bukaKoneksi().use { conn ->
                val sql = "UPDATE outbox_sinkronisasi SET status = ?, pesan_gagal_terakhir = ?, diperbarui_pada = CURRENT_TIMESTAMP WHERE id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, StatusOutboxSinkronisasi.KONFLIK.name)
                    ps.setString(2, pesan)
                    ps.setString(3, id)
                    ps.executeUpdate()
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menandai outbox konflik: ${e.message}"))
        }
    }

    fun hapusBerhasilLebihLamaDari(hari: Int): HasilOperasi<Int> {
        return try {
            koneksi.bukaKoneksi().use { conn ->
                // SQLite tidak punya fungsi DATE_SUB secara bawaan yang standar, pakai strftime atau date()
                val sql = "DELETE FROM outbox_sinkronisasi WHERE status = 'BERHASIL' AND dibuat_pada < date('now', '-$hari days')"
                conn.createStatement().use { stmt ->
                    val jumlah = stmt.executeUpdate(sql)
                    HasilOperasi.Berhasil(jumlah)
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menghapus outbox lama: ${e.message}"))
        }
    }

    private fun perbaruiStatus(id: String, status: StatusOutboxSinkronisasi): HasilOperasi<Unit> {
        return try {
            koneksi.bukaKoneksi().use { conn ->
                val sql = "UPDATE outbox_sinkronisasi SET status = ?, diperbarui_pada = CURRENT_TIMESTAMP WHERE id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, status.name)
                    ps.setString(2, id)
                    ps.executeUpdate()
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal memperbarui status outbox: ${e.message}"))
        }
    }
}
