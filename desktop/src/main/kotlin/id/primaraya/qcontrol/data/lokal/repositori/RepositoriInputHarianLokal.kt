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
                val sqlCek = "SELECT * FROM pemeriksaan_harian_draft WHERE tanggal_produksi = ? AND line_produksi_id = ?"
                koneksi.prepareStatement(sqlCek).use { ps ->
                    ps.setString(1, tanggalProduksi)
                    ps.setString(2, lineId)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        HasilOperasi.Berhasil(
                            DraftPemeriksaanHarian(
                                id = rs.getString("id"),
                                clientDraftId = rs.getString("client_draft_id"),
                                tanggalProduksi = rs.getString("tanggal_produksi"),
                                lineId = rs.getString("line_produksi_id"),
                                nomorDokumen = rs.getString("nomor_dokumen"),
                                revisi = rs.getString("revisi"),
                                catatan = rs.getString("catatan"),
                                statusDraft = rs.getString("status_draft"),
                                idempotencyKey = rs.getString("idempotency_key"),
                                hashPayload = rs.getString("hash_payload"),
                                terakhirDisimpanPada = rs.getString("terakhir_disimpan_pada"),
                                terakhirDikirimPada = rs.getString("terakhir_dikirim_pada"),
                                pesanErrorTerakhir = rs.getString("pesan_error_terakhir"),
                                dibuatPada = rs.getString("created_at"),
                                diperbaruiPada = rs.getString("updated_at")
                            )
                        )
                    } else {
                        // Buat baru
                        val idBaru = UUID.randomUUID().toString()
                        val clientDraftId = "DRAFT-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}"
                        val idempotencyKey = UUID.randomUUID().toString()
                        val waktuSekarang = Instant.now().toString()
                        
                        val sqlInsert = """
                            INSERT INTO pemeriksaan_harian_draft (
                                id, client_draft_id, tanggal_produksi, line_produksi_id, 
                                status_draft, idempotency_key, terakhir_disimpan_pada, 
                                created_at, updated_at
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent()
                        
                        koneksi.prepareStatement(sqlInsert).use { psInsert ->
                            psInsert.setString(1, idBaru)
                            psInsert.setString(2, clientDraftId)
                            psInsert.setString(3, tanggalProduksi)
                            psInsert.setString(4, lineId)
                            psInsert.setString(5, "DRAFT")
                            psInsert.setString(6, idempotencyKey)
                            psInsert.setString(7, waktuSekarang)
                            psInsert.setString(8, waktuSekarang)
                            psInsert.setString(9, waktuSekarang)
                            psInsert.executeUpdate()
                        }
                        
                        HasilOperasi.Berhasil(
                            DraftPemeriksaanHarian(
                                id = idBaru,
                                clientDraftId = clientDraftId,
                                tanggalProduksi = tanggalProduksi,
                                lineId = lineId,
                                statusDraft = "DRAFT",
                                idempotencyKey = idempotencyKey,
                                terakhirDisimpanPada = waktuSekarang,
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

    fun ambilDraftInputPart(pemeriksaanHarianId: String, kataKunci: String = ""): HasilOperasi<List<DraftInputPart>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    SELECT d.*, m.nama_part, m.nomor_part
                    FROM pemeriksaan_part_draft d
                    JOIN master_part m ON d.part_id = m.id
                    WHERE d.pemeriksaan_harian_draft_id = ?
                    AND (m.nama_part LIKE ? OR m.nomor_part LIKE ?)
                    ORDER BY d.urutan_tampil ASC, m.nama_part ASC
                """.trimIndent()
                
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    ps.setString(2, "%$kataKunci%")
                    ps.setString(3, "%$kataKunci%")
                    val rs = ps.executeQuery()
                    val daftar = mutableListOf<DraftInputPart>()
                    while (rs.next()) {
                        daftar.add(
                            DraftInputPart(
                                id = rs.getString("id"),
                                pemeriksaanHarianId = rs.getString("pemeriksaan_harian_draft_id"),
                                partId = rs.getString("part_id"),
                                namaPart = rs.getString("nama_part"),
                                nomorPart = rs.getString("nomor_part"),
                                qtyCheck = rs.getInt("total_check"),
                                totalOk = rs.getInt("total_ok"),
                                totalDefect = rs.getInt("total_defect"),
                                rasioDefect = rs.getDouble("rasio_defect"),
                                urutanTampil = rs.getInt("urutan_tampil")
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

    fun ambilDraftInputDefectSlot(inputPartId: String): HasilOperasi<List<DraftInputDefectSlot>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    SELECT d.*, m.nama_defect
                    FROM pemeriksaan_defect_slot_draft d
                    JOIN master_relasi_part_defect r ON d.relasi_part_defect_id = r.id
                    JOIN master_jenis_defect m ON r.jenis_defect_id = m.id
                    WHERE d.pemeriksaan_part_draft_id = ?
                """.trimIndent()
                
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, inputPartId)
                    val rs = ps.executeQuery()
                    val daftar = mutableListOf<DraftInputDefectSlot>()
                    while (rs.next()) {
                        daftar.add(
                            DraftInputDefectSlot(
                                id = rs.getString("id"),
                                inputPartId = rs.getString("pemeriksaan_part_draft_id"),
                                relasiPartDefectId = rs.getString("relasi_part_defect_id"),
                                slotWaktuId = rs.getString("slot_waktu_id"),
                                namaDefect = rs.getString("nama_defect"),
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

    fun ambilDraftProduksiTanpaNg(pemeriksaanHarianId: String): HasilOperasi<List<DraftProduksiTanpaNg>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    SELECT d.*, m.nama_part, m.nomor_part
                    FROM pemeriksaan_produksi_tanpa_ng_draft d
                    JOIN master_part m ON d.part_id = m.id
                    WHERE d.pemeriksaan_harian_draft_id = ?
                    ORDER BY m.nama_part ASC
                """.trimIndent()
                
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    val daftar = mutableListOf<DraftProduksiTanpaNg>()
                    while (rs.next()) {
                        daftar.add(
                            DraftProduksiTanpaNg(
                                partId = rs.getString("part_id"),
                                namaPart = rs.getString("nama_part"),
                                nomorPart = rs.getString("nomor_part"),
                                totalProduksi = rs.getInt("total_produksi"),
                                catatan = rs.getString("catatan")
                            )
                        )
                    }
                    HasilOperasi.Berhasil(daftar)
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft produksi tanpa NG: ${e.message}"))
        }
    }

    suspend fun updateProduksiTanpaNg(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val waktuSekarang = Instant.now().toString()
                val sqlUpsert = """
                    INSERT INTO pemeriksaan_produksi_tanpa_ng_draft (
                        id, pemeriksaan_harian_draft_id, part_id, total_produksi, 
                        created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT(pemeriksaan_harian_draft_id, part_id) DO UPDATE SET
                    total_produksi = excluded.total_produksi,
                    updated_at = excluded.updated_at
                """.trimIndent()
                
                koneksi.prepareStatement(sqlUpsert).use { ps ->
                    ps.setString(1, UUID.randomUUID().toString())
                    ps.setString(2, pemeriksaanHarianId)
                    ps.setString(3, partId)
                    ps.setInt(4, qty)
                    ps.setString(5, waktuSekarang)
                    ps.setString(6, waktuSekarang)
                    ps.executeUpdate()
                }
                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update produksi tanpa NG: ${e.message}"))
        }
    }

    suspend fun updateQtyCheck(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                koneksi.autoCommit = false
                try {
                    val waktuSekarang = Instant.now().toString()
                    
                    // 1. Upsert Part Draft
                    val sqlUpsert = """
                        INSERT INTO pemeriksaan_part_draft (
                            id, pemeriksaan_harian_draft_id, part_id, total_check, 
                            total_ok, total_defect, created_at, updated_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT(pemeriksaan_harian_draft_id, part_id) DO UPDATE SET
                        total_check = excluded.total_check,
                        total_ok = excluded.total_check - total_defect,
                        updated_at = excluded.updated_at
                    """.trimIndent()
                    
                    koneksi.prepareStatement(sqlUpsert).use { ps ->
                        ps.setString(1, UUID.randomUUID().toString())
                        ps.setString(2, pemeriksaanHarianId)
                        ps.setString(3, partId)
                        ps.setInt(4, qty)
                        ps.setInt(5, qty) // Initial OK = Check
                        ps.setInt(6, 0)
                        ps.setString(7, waktuSekarang)
                        ps.setString(8, waktuSekarang)
                        ps.executeUpdate()
                    }
                    
                    // 2. Update Harian Timestamp
                    val sqlUpdateHarian = "UPDATE pemeriksaan_harian_draft SET terakhir_disimpan_pada = ?, updated_at = ? WHERE id = ?"
                    koneksi.prepareStatement(sqlUpdateHarian).use { ps ->
                        ps.setString(1, waktuSekarang)
                        ps.setString(2, waktuSekarang)
                        ps.setString(3, pemeriksaanHarianId)
                        ps.executeUpdate()
                    }
                    
                    koneksi.commit()
                    HasilOperasi.Berhasil(Unit)
                } catch (e: Exception) {
                    koneksi.rollback()
                    throw e
                } finally {
                    koneksi.autoCommit = true
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update qty check: ${e.message}"))
        }
    }

    suspend fun updateDefectSlot(
        pemeriksaanHarianId: String,
        partId: String,
        relasiPartDefectId: String,
        slotWaktuId: String,
        qty: Int
    ): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                koneksi.autoCommit = false
                try {
                    val waktuSekarang = Instant.now().toString()
                    
                    // 1. Dapatkan/Pastikan Part Draft Ada
                    val sqlCekPart = "SELECT id FROM pemeriksaan_part_draft WHERE pemeriksaan_harian_draft_id = ? AND part_id = ?"
                    var partDraftId = ""
                    koneksi.prepareStatement(sqlCekPart).use { ps ->
                        ps.setString(1, pemeriksaanHarianId)
                        ps.setString(2, partId)
                        val rs = ps.executeQuery()
                        if (rs.next()) {
                            partDraftId = rs.getString("id")
                        }
                    }
                    
                    if (partDraftId.isEmpty()) {
                        partDraftId = UUID.randomUUID().toString()
                        val sqlInsertPart = """
                            INSERT INTO pemeriksaan_part_draft (
                                id, pemeriksaan_harian_draft_id, part_id, created_at, updated_at
                            ) VALUES (?, ?, ?, ?, ?)
                        """.trimIndent()
                        koneksi.prepareStatement(sqlInsertPart).use { ps ->
                            ps.setString(1, partDraftId)
                            ps.setString(2, pemeriksaanHarianId)
                            ps.setString(3, partId)
                            ps.setString(4, waktuSekarang)
                            ps.setString(5, waktuSekarang)
                            ps.executeUpdate()
                        }
                    }
                    
                    // 2. Upsert Defect Slot
                    val sqlUpsertDefect = """
                        INSERT INTO pemeriksaan_defect_slot_draft (
                            id, pemeriksaan_part_draft_id, relasi_part_defect_id, 
                            slot_waktu_id, jumlah_defect, created_at, updated_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT(pemeriksaan_part_draft_id, relasi_part_defect_id, slot_waktu_id) 
                        DO UPDATE SET
                        jumlah_defect = excluded.jumlah_defect,
                        updated_at = excluded.updated_at
                    """.trimIndent()
                    
                    koneksi.prepareStatement(sqlUpsertDefect).use { ps ->
                        ps.setString(1, UUID.randomUUID().toString())
                        ps.setString(2, partDraftId)
                        ps.setString(3, relasiPartDefectId)
                        ps.setString(4, slotWaktuId)
                        ps.setInt(5, qty)
                        ps.setString(6, waktuSekarang)
                        ps.setString(7, waktuSekarang)
                        ps.executeUpdate()
                    }
                    
                    // 3. Recalculate Totals for Part
                    val sqlRecalc = """
                        UPDATE pemeriksaan_part_draft SET
                        total_defect = (SELECT COALESCE(SUM(jumlah_defect), 0) FROM pemeriksaan_defect_slot_draft WHERE pemeriksaan_part_draft_id = ?),
                        updated_at = ?
                        WHERE id = ?
                    """.trimIndent()
                    koneksi.prepareStatement(sqlRecalc).use { ps ->
                        ps.setString(1, partDraftId)
                        ps.setString(2, waktuSekarang)
                        ps.setString(3, partDraftId)
                        ps.executeUpdate()
                    }
                    
                    val sqlFinalUpdate = """
                        UPDATE pemeriksaan_part_draft SET
                        total_ok = total_check - total_defect,
                        rasio_defect = CASE WHEN total_check > 0 THEN (CAST(total_defect AS REAL) / total_check) * 100.0 ELSE 0.0 END
                        WHERE id = ?
                    """.trimIndent()
                    koneksi.prepareStatement(sqlFinalUpdate).use { ps ->
                        ps.setString(1, partDraftId)
                        ps.executeUpdate()
                    }

                    // 4. Update Harian Timestamp
                    val sqlUpdateHarian = "UPDATE pemeriksaan_harian_draft SET terakhir_disimpan_pada = ?, updated_at = ? WHERE id = ?"
                    koneksi.prepareStatement(sqlUpdateHarian).use { ps ->
                        ps.setString(1, waktuSekarang)
                        ps.setString(2, waktuSekarang)
                        ps.setString(3, pemeriksaanHarianId)
                        ps.executeUpdate()
                    }
                    
                    koneksi.commit()
                    HasilOperasi.Berhasil(Unit)
                } catch (e: Exception) {
                    koneksi.rollback()
                    throw e
                } finally {
                    koneksi.autoCommit = true
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update defect slot: ${e.message}"))
        }
    }

    fun hitungRingkasan(pemeriksaanHarianId: String): HasilOperasi<RingkasanInputHarian> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                // 0. Ambil Line ID untuk hitung total part
                var lineId = ""
                val sqlLine = "SELECT line_produksi_id FROM pemeriksaan_harian_draft WHERE id = ?"
                koneksi.prepareStatement(sqlLine).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    if (rs.next()) lineId = rs.getString("line_produksi_id")
                }

                // 1. Hitung Part Sudah Diisi (total_check > 0)
                var partSudahDiisi = 0
                val sqlSudah = "SELECT COUNT(*) FROM pemeriksaan_part_draft WHERE pemeriksaan_harian_draft_id = ? AND total_check > 0"
                koneksi.prepareStatement(sqlSudah).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    if (rs.next()) partSudahDiisi = rs.getInt(1)
                }

                // 2. Hitung Total Part di Master untuk Line ini
                var totalPartMaster = 0
                val sqlMaster = "SELECT COUNT(*) FROM master_part WHERE line_default_id = ? OR kode_line_default = (SELECT kode_line FROM master_line_produksi WHERE id = ?)"
                koneksi.prepareStatement(sqlMaster).use { ps ->
                    ps.setString(1, lineId)
                    ps.setString(2, lineId)
                    val rs = ps.executeQuery()
                    if (rs.next()) totalPartMaster = rs.getInt(1)
                }

                val sqlTotal = """
                    SELECT SUM(total_check) as t_check, SUM(total_defect) as t_defect
                    FROM pemeriksaan_part_draft
                    WHERE pemeriksaan_harian_draft_id = ?
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
                
                val sqlDefect = """
                    SELECT m.nama_defect, SUM(d.jumlah_defect) as jumlah
                    FROM pemeriksaan_defect_slot_draft d
                    JOIN pemeriksaan_part_draft p ON d.pemeriksaan_part_draft_id = p.id
                    JOIN master_relasi_part_defect r ON d.relasi_part_defect_id = r.id
                    JOIN master_jenis_defect m ON r.jenis_defect_id = m.id
                    WHERE p.pemeriksaan_harian_draft_id = ?
                    GROUP BY m.nama_defect
                    HAVING jumlah > 0
                """.trimIndent()
                
                val daftarDefect = mutableListOf<DefectTerhitung>()
                koneksi.prepareStatement(sqlDefect).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftarDefect.add(DefectTerhitung(rs.getString("nama_defect"), rs.getInt("jumlah")))
                    }
                }

                val sqlPerSlot = """
                    SELECT s.id, s.label_slot, SUM(d.jumlah_defect) as jumlah
                    FROM pemeriksaan_defect_slot_draft d
                    JOIN pemeriksaan_part_draft p ON d.pemeriksaan_part_draft_id = p.id
                    JOIN master_slot_waktu s ON d.slot_waktu_id = s.id
                    WHERE p.pemeriksaan_harian_draft_id = ?
                    GROUP BY s.id, s.label_slot
                    HAVING jumlah > 0
                """.trimIndent()

                val daftarPerSlot = mutableListOf<DefectPerSlotTerhitung>()
                koneksi.prepareStatement(sqlPerSlot).use { ps ->
                    ps.setString(1, pemeriksaanHarianId)
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftarPerSlot.add(DefectPerSlotTerhitung(rs.getString("id"), rs.getString("label_slot"), rs.getInt("jumlah")))
                    }
                }

                HasilOperasi.Berhasil(
                    RingkasanInputHarian(
                        totalQtyCheck = totalQtyCheck, 
                        totalQtyDefect = totalQtyDefect, 
                        totalPartSudahDiisi = partSudahDiisi,
                        totalPartBelumDiisi = (totalPartMaster - partSudahDiisi).coerceAtLeast(0),
                        daftarDefect = daftarDefect, 
                        daftarPerSlot = daftarPerSlot
                    )
                )
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal hitung ringkasan: ${e.message}"))
        }
    }

    fun hapusDraft(pemeriksaanHarianId: String): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                koneksi.autoCommit = false
                try {
                    // Cascade delete manually since we didn't define CASCADE in migration (good practice in some cases)
                    val sql1 = "DELETE FROM pemeriksaan_defect_slot_draft WHERE pemeriksaan_part_draft_id IN (SELECT id FROM pemeriksaan_part_draft WHERE pemeriksaan_harian_draft_id = ?)"
                    val sql2 = "DELETE FROM pemeriksaan_part_draft WHERE pemeriksaan_harian_draft_id = ?"
                    val sql3 = "DELETE FROM pemeriksaan_harian_draft WHERE id = ?"
                    
                    koneksi.prepareStatement(sql1).use { ps -> ps.setString(1, pemeriksaanHarianId); ps.executeUpdate() }
                    koneksi.prepareStatement(sql2).use { ps -> ps.setString(1, pemeriksaanHarianId); ps.executeUpdate() }
                    koneksi.prepareStatement(sql3).use { ps -> ps.setString(1, pemeriksaanHarianId); ps.executeUpdate() }
                    
                    koneksi.commit()
                    HasilOperasi.Berhasil(Unit)
                } catch (e: Exception) {
                    koneksi.rollback()
                    throw e
                } finally {
                    koneksi.autoCommit = true
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal hapus draft: ${e.message}"))
        }
    }

    fun updateStatusDraft(id: String, status: String, hash: String?): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "UPDATE pemeriksaan_harian_draft SET status_draft = ?, hash_payload = ?, updated_at = ? WHERE id = ?"
                koneksi.prepareStatement(sql).use { ps ->
                    ps.setString(1, status)
                    ps.setString(2, hash)
                    ps.setString(3, Instant.now().toString())
                    ps.setString(4, id)
                    ps.executeUpdate()
                }
                HasilOperasi.Berhasil(Unit)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update status draft: ${e.message}"))
        }
    }
}
