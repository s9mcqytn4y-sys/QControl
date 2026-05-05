package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.*
import java.time.Instant
import java.util.UUID

class RepositoriInputHarianLokal(
    private val koneksiDatabase: KoneksiDatabaseLokal
) {
    fun ambilAtauBuatDraft(tanggalProduksi: String, lineId: String): HasilOperasi<DraftPemeriksaanHarian> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sqlCek = "SELECT * FROM draft_pemeriksaan_harian WHERE tanggal_produksi = ? AND line_id = ?"
                koneksi.prepareStatement(sqlCek).use { ps ->
                    ps.setString(1, tanggalProduksi)
                    ps.setString(2, lineId)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        HasilOperasi.Berhasil(
                            DraftPemeriksaanHarian(
                                id = rs.getString("id"),
                                tanggalProduksi = rs.getString("tanggal_produksi"),
                                lineId = rs.getString("line_id"),
                                catatan = rs.getString("catatan"),
                                dibuatPada = rs.getString("dibuat_pada"),
                                diperbaruiPada = rs.getString("diperbarui_pada")
                            )
                        )
                    } else {
                        // Buat baru
                        val idBaru = UUID.randomUUID().toString()
                        val waktuSekarang = Instant.now().toString()
                        val sqlInsert = """
                            INSERT INTO draft_pemeriksaan_harian (id, tanggal_produksi, line_id, dibuat_pada, diperbarui_pada)
                            VALUES (?, ?, ?, ?, ?)
                        """.trimIndent()
                        koneksi.prepareStatement(sqlInsert).use { psInsert ->
                            psInsert.setString(1, idBaru)
                            psInsert.setString(2, tanggalProduksi)
                            psInsert.setString(3, lineId)
                            psInsert.setString(4, waktuSekarang)
                            psInsert.setString(5, waktuSekarang)
                            psInsert.executeUpdate()
                        }
                        HasilOperasi.Berhasil(
                            DraftPemeriksaanHarian(
                                id = idBaru,
                                tanggalProduksi = tanggalProduksi,
                                lineId = lineId,
                                catatan = null,
                                dibuatPada = waktuSekarang,
                                diperbaruiPada = waktuSekarang
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil/buat draft: ${e.message}"))
        }
    }

    fun ambilDraftInputPart(pemeriksaanHarianId: String): HasilOperasi<List<DraftInputPart>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "SELECT * FROM draft_input_part WHERE pemeriksaan_harian_id = ?"
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    val daftar = mutableListOf<DraftInputPart>()
                    while (rs.next()) {
                        daftar.add(
                            DraftInputPart(
                                id = rs.getString("id"),
                                pemeriksaanHarianId = rs.getString("pemeriksaan_harian_id"),
                                partId = rs.getString("part_id"),
                                qtyCheck = rs.getInt("total_check"),
                                totalOk = rs.getInt("total_ok"),
                                totalDefect = rs.getInt("total_defect")
                            )
                        )
                    }
                    HasilOperasi.Berhasil(daftar)
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft input part: ${e.message}"))
        }
    }

    fun ambilInputPartSpesifik(pemeriksaanHarianId: String, partId: String): HasilOperasi<DraftInputPart?> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "SELECT * FROM draft_input_part WHERE pemeriksaan_harian_id = ? AND part_id = ?"
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    ps.setString(2, partId)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        HasilOperasi.Berhasil(
                            DraftInputPart(
                            id = rs.getString("id"),
                            pemeriksaanHarianId = rs.getString("pemeriksaan_harian_id"),
                            partId = rs.getString("part_id"),
                            qtyCheck = rs.getInt("total_check"),
                            totalOk = rs.getInt("total_ok"),
                            totalDefect = rs.getInt("total_defect")
                        )
                        )
                    } else {
                        HasilOperasi.Berhasil(null)
                    }
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil input part spesifik: ${e.message}"))
        }
    }

    fun ambilDraftInputDefectSlot(inputPartId: String): HasilOperasi<List<DraftInputDefectSlot>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "SELECT * FROM draft_input_defect_slot WHERE input_part_id = ?"
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, inputPartId)
                    val rs = ps.executeQuery()
                    val daftar = mutableListOf<DraftInputDefectSlot>()
                    while (rs.next()) {
                        daftar.add(
                            DraftInputDefectSlot(
                                id = rs.getString("id"),
                                inputPartId = rs.getString("input_part_id"),
                                relasiPartDefectId = rs.getString("relasi_part_defect_id"),
                                slotWaktuId = rs.getString("slot_waktu_id"),
                                jumlahDefect = rs.getInt("jumlah_defect")
                            )
                        )
                    }
                    HasilOperasi.Berhasil(daftar)
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft defect slot: ${e.message}"))
        }
    }

    fun simpanAtauUpdateInputPart(item: DraftInputPart): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    INSERT INTO draft_input_part (id, pemeriksaan_harian_id, part_id, total_check, total_ok, total_defect)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT(pemeriksaan_harian_id, part_id) DO UPDATE SET
                    total_check = excluded.total_check,
                    total_ok = excluded.total_ok,
                    total_defect = excluded.total_defect
                """.trimIndent()
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, item.id)
                    ps.setString(2, item.pemeriksaanHarianId)
                    ps.setString(3, item.partId)
                    ps.setInt(4, item.qtyCheck)
                    ps.setInt(5, item.totalOk)
                    ps.setInt(6, item.totalDefect)
                    ps.executeUpdate()
                }
                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal simpan input part: ${e.message}"))
        }
    }

    fun simpanAtauUpdateDefectSlot(item: DraftInputDefectSlot): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    INSERT INTO draft_input_defect_slot (id, input_part_id, relasi_part_defect_id, slot_waktu_id, jumlah_defect)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT(input_part_id, relasi_part_defect_id, slot_waktu_id) DO UPDATE SET
                    jumlah_defect = excluded.jumlah_defect
                """.trimIndent()
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, item.id)
                    ps.setString(2, item.inputPartId)
                    ps.setString(3, item.relasiPartDefectId)
                    ps.setString(4, item.slotWaktuId)
                    ps.setInt(5, item.jumlahDefect)
                    ps.executeUpdate()
                }
                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal simpan defect slot: ${e.message}"))
        }
    }

    fun hitungRingkasan(pemeriksaanHarianId: String): HasilOperasi<RingkasanInputHarian> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                // 1. Ambil Total
                val sqlTotal = """
                    SELECT SUM(total_check) as t_check, SUM(total_defect) as t_defect
                    FROM draft_input_part
                    WHERE pemeriksaan_harian_id = ?
                """.trimIndent()
                
                var totalQtyCheck = 0
                var totalQtyDefect = 0
                
                koneksi.prepareStatement(sqlTotal).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        totalQtyCheck = rs.getInt("t_check")
                        totalQtyDefect = rs.getInt("t_defect")
                    }
                }
                
                // 2. Ambil Daftar Defect Teragregasi
                val sqlDefect = """
                    SELECT m.nama_defect, SUM(d.jumlah_defect) as jumlah
                    FROM draft_input_defect_slot d
                    JOIN draft_input_part p ON d.input_part_id = p.id
                    JOIN master_relasi_part_defect r ON d.relasi_part_defect_id = r.id
                    JOIN master_jenis_defect m ON r.jenis_defect_id = m.id
                    WHERE p.pemeriksaan_harian_id = ?
                    GROUP BY m.nama_defect
                    HAVING jumlah > 0
                """.trimIndent()
                // Catatan: JOIN di atas mungkin perlu disesuaikan jika relasi_part_defect_id bukan ID defect langsung.
                // Namun untuk fase ini, kita fokus pada kelancaran kompilasi dan flow dasar.
                
                val daftarDefect = mutableListOf<DefectTerhitung>()
                koneksi.prepareStatement(sqlDefect).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftarDefect.add(DefectTerhitung(rs.getString("nama_defect"), rs.getInt("jumlah")))
                    }
                }

                HasilOperasi.Berhasil(RingkasanInputHarian(totalQtyCheck, totalQtyDefect, daftarDefect))
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal hitung ringkasan: ${e.message}"))
        }
    }

    suspend fun simpanAtauPerbaruiPart(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    INSERT INTO draft_input_part (id, pemeriksaan_harian_id, part_id, total_check, total_ok)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT(pemeriksaan_harian_id, part_id) DO UPDATE SET
                    total_check = excluded.total_check,
                    total_ok = excluded.total_check - total_defect
                """.trimIndent()
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, UUID.randomUUID().toString())
                    ps.setString(2, pemeriksaanHarianId)
                    ps.setString(3, partId)
                    ps.setInt(4, qty)
                    ps.setInt(5, qty) 
                    ps.executeUpdate()
                }
                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal simpan part: ${e.message}"))
        }
    }

    suspend fun simpanAtauPerbaruiDefect(pemeriksaanHarianId: String, partId: String, relasiPartDefectId: String, qty: Int): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sqlPart = "SELECT id FROM draft_input_part WHERE pemeriksaan_harian_id = ? AND part_id = ?"
                var inputPartId = ""
                koneksi.prepareStatement(sqlPart).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    ps.setString(2, partId)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        inputPartId = rs.getString("id")
                    }
                }

                if (inputPartId.isEmpty()) {
                    return HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Part belum terdaftar di draft"))
                }

                val sqlDefect = """
                    INSERT INTO draft_input_defect_slot (id, input_part_id, relasi_part_defect_id, slot_waktu_id, jumlah_defect)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT(input_part_id, relasi_part_defect_id, slot_waktu_id) DO UPDATE SET
                    jumlah_defect = excluded.jumlah_defect
                """.trimIndent()
                
                koneksi.prepareStatement(sqlDefect).use { ps ->
                    ps.setString(1, UUID.randomUUID().toString())
                    ps.setString(2, inputPartId)
                    ps.setString(3, relasiPartDefectId)
                    ps.setString(4, "SLOT-01") 
                    ps.setInt(5, qty)
                    ps.executeUpdate()
                }

                val sqlUpdateTotal = """
                    UPDATE draft_input_part 
                    SET total_defect = (SELECT COALESCE(SUM(jumlah_defect), 0) FROM draft_input_defect_slot WHERE input_part_id = ?),
                        total_ok = total_check - (SELECT COALESCE(SUM(jumlah_defect), 0) FROM draft_input_defect_slot WHERE input_part_id = ?)
                    WHERE id = ?
                """.trimIndent()
                koneksi.prepareStatement(sqlUpdateTotal).use { ps ->
                    ps.setString(1, inputPartId)
                    ps.setString(2, inputPartId)
                    ps.setString(3, inputPartId)
                    ps.executeUpdate()
                }

                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal simpan defect: ${e.message}"))
        }
    }
}
