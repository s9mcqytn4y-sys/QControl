package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.MigrasiDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.*
import java.time.Instant

class RepositoriMasterDataLokal(
    private val koneksiDatabase: KoneksiDatabaseLokal,
    private val migrasiDatabaseLokal: MigrasiDatabaseLokal
) {
    init {
        migrasiDatabaseLokal.jalankanMigrasi()
    }

    fun simpanMasterData(masterData: MasterDataQControl): HasilOperasi<Unit> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                koneksi.autoCommit = false
                try {
                    val waktuSekarang = Instant.now().toString()

                    // Hapus dan isi ulang semua tabel master dalam satu transaksi
                    koneksi.createStatement().use { st ->
                        st.execute("DELETE FROM master_relasi_part_defect")
                        st.execute("DELETE FROM master_jenis_defect")
                        st.execute("DELETE FROM master_kategori_defect")
                        st.execute("DELETE FROM master_part")
                        st.execute("DELETE FROM master_material")
                        st.execute("DELETE FROM master_slot_waktu")
                        st.execute("DELETE FROM master_line_produksi")
                    }

                    // Simpan line produksi
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_line_produksi (id, kode_line, nama_line, aktif, urutan_tampil) VALUES (?,?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.lineProduksi) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeLine)
                            ps.setString(3, item.namaLine)
                            ps.setInt(4, if (item.aktif) 1 else 0)
                            ps.setInt(5, item.urutanTampil)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan slot waktu
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_slot_waktu (id, kode_slot, label_slot, jam_mulai, jam_selesai, aktif, urutan_tampil) VALUES (?,?,?,?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.slotWaktu) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeSlot)
                            ps.setString(3, item.labelSlot)
                            ps.setString(4, item.jamMulai)
                            ps.setString(5, item.jamSelesai)
                            ps.setInt(6, if (item.aktif) 1 else 0)
                            ps.setInt(7, item.urutanTampil)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan material
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_material (id, kode_material, nama_material, aktif) VALUES (?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.material) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeMaterial)
                            ps.setString(3, item.namaMaterial)
                            ps.setInt(4, if (item.aktif) 1 else 0)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan part
                    koneksi.prepareStatement(
                        """INSERT OR REPLACE INTO master_part 
                           (id, kode_unik_part, nama_part, nomor_part, material_id, kode_material, nama_material,
                            kode_proyek, jumlah_item_per_kanban, line_default_id, kode_line_default, nama_line_default,
                            aktif, sumber_data) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"""
                    ).use { ps ->
                        for (item in masterData.part) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeUnikPart)
                            ps.setString(3, item.namaPart)
                            ps.setString(4, item.nomorPart)
                            ps.setString(5, item.materialId)
                            ps.setString(6, item.kodeMaterial)
                            ps.setString(7, item.namaMaterial)
                            ps.setString(8, item.kodeProyek)
                            if (item.jumlahItemPerKanban != null) ps.setInt(9, item.jumlahItemPerKanban) else ps.setNull(9, java.sql.Types.INTEGER)
                            ps.setString(10, item.lineDefaultId)
                            ps.setString(11, item.kodeLineDefault)
                            ps.setString(12, item.namaLineDefault)
                            ps.setInt(13, if (item.aktif) 1 else 0)
                            ps.setString(14, item.sumberData)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan kategori defect
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_kategori_defect (id, kode_kategori, nama_kategori, aktif, urutan_tampil) VALUES (?,?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.kategoriDefect) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeKategori)
                            ps.setString(3, item.namaKategori)
                            ps.setInt(4, if (item.aktif) 1 else 0)
                            ps.setInt(5, item.urutanTampil)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan jenis defect
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_jenis_defect (id, kode_defect, nama_defect, kategori_defect_id, kode_kategori, nama_kategori, aktif) VALUES (?,?,?,?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.jenisDefect) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.kodeDefect)
                            ps.setString(3, item.namaDefect)
                            ps.setString(4, item.kategoriDefectId)
                            ps.setString(5, item.kodeKategori)
                            ps.setString(6, item.namaKategori)
                            ps.setInt(7, if (item.aktif) 1 else 0)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan relasi part-defect
                    koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO master_relasi_part_defect (id, part_id, kode_unik_part, jenis_defect_id, kode_defect, urutan_tampil, aktif) VALUES (?,?,?,?,?,?,?)"
                    ).use { ps ->
                        for (item in masterData.relasiPartDefect) {
                            ps.setString(1, item.id)
                            ps.setString(2, item.partId)
                            ps.setString(3, item.kodeUnikPart)
                            ps.setString(4, item.jenisDefectId)
                            ps.setString(5, item.kodeDefect)
                            ps.setInt(6, item.urutanTampil)
                            ps.setInt(7, if (item.aktif) 1 else 0)
                            ps.addBatch()
                        }
                        ps.executeBatch()
                    }

                    // Simpan metadata
                    val simpanMetadata = koneksi.prepareStatement(
                        "INSERT OR REPLACE INTO metadata_master_data (kunci, nilai, diperbarui_pada) VALUES (?,?,?)"
                    )
                    fun simpanKunci(kunci: String, nilai: String) {
                        simpanMetadata.setString(1, kunci)
                        simpanMetadata.setString(2, nilai)
                        simpanMetadata.setString(3, waktuSekarang)
                        simpanMetadata.addBatch()
                    }
                    simpanKunci("versiMasterData", masterData.versiMasterData)
                    simpanKunci("ditarikPada", waktuSekarang)
                    simpanKunci("jumlahLineProduksi", masterData.lineProduksi.size.toString())
                    simpanKunci("jumlahSlotWaktu", masterData.slotWaktu.size.toString())
                    simpanKunci("jumlahMaterial", masterData.material.size.toString())
                    simpanKunci("jumlahPart", masterData.part.size.toString())
                    simpanKunci("jumlahJenisDefect", masterData.jenisDefect.size.toString())
                    simpanKunci("jumlahRelasiPartDefect", masterData.relasiPartDefect.size.toString())
                    simpanKunci("jumlahShiftOperasional", "1")
                    simpanMetadata.use { it.executeBatch() }

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
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal menyimpan master data: ${e.message}"))
        }
    }

    fun bacaRingkasanMasterData(): HasilOperasi<RingkasanMasterData> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val metadata = mutableMapOf<String, String>()
                koneksi.createStatement().use { st ->
                    val rs = st.executeQuery("SELECT kunci, nilai FROM metadata_master_data")
                    while (rs.next()) {
                        metadata[rs.getString("kunci")] = rs.getString("nilai")
                    }
                }

                if (metadata.isEmpty()) {
                    return HasilOperasi.Gagal(KesalahanAplikasi.DataTidakDitemukan("Master data belum pernah ditarik dari server"))
                }

                HasilOperasi.Berhasil(
                    RingkasanMasterData(
                        versiMasterData = metadata["versiMasterData"] ?: "-",
                        ditarikPada = metadata["ditarikPada"] ?: "-",
                        jumlahLineProduksi = metadata["jumlahLineProduksi"]?.toIntOrNull() ?: 0,
                        jumlahSlotWaktu = metadata["jumlahSlotWaktu"]?.toIntOrNull() ?: 0,
                        jumlahMaterial = metadata["jumlahMaterial"]?.toIntOrNull() ?: 0,
                        jumlahPart = metadata["jumlahPart"]?.toIntOrNull() ?: 0,
                        jumlahJenisDefect = metadata["jumlahJenisDefect"]?.toIntOrNull() ?: 0,
                        jumlahRelasiPartDefect = metadata["jumlahRelasiPartDefect"]?.toIntOrNull() ?: 0,
                        jumlahShiftOperasional = metadata["jumlahShiftOperasional"]?.toIntOrNull() ?: 1
                    )
                )
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca ringkasan master data: ${e.message}"))
        }
    }

    fun bacaDaftarPart(kataKunci: String = ""): HasilOperasi<List<Part>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = if (kataKunci.isBlank()) {
                    "SELECT * FROM master_part ORDER BY nama_part"
                } else {
                    "SELECT * FROM master_part WHERE nama_part LIKE ? OR kode_unik_part LIKE ? ORDER BY nama_part"
                }
                val daftar = mutableListOf<Part>()
                koneksi.prepareStatement(sql).use { ps ->
                    if (kataKunci.isNotBlank()) {
                        val pola = "%$kataKunci%"
                        ps.setString(1, pola)
                        ps.setString(2, pola)
                    }
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftar.add(
                            Part(
                                id = rs.getString("id"),
                                kodeUnikPart = rs.getString("kode_unik_part"),
                                namaPart = rs.getString("nama_part"),
                                nomorPart = rs.getString("nomor_part"),
                                materialId = rs.getString("material_id"),
                                kodeMaterial = rs.getString("kode_material"),
                                namaMaterial = rs.getString("nama_material"),
                                kodeProyek = rs.getString("kode_proyek"),
                                jumlahItemPerKanban = rs.getObject("jumlah_item_per_kanban") as? Int,
                                lineDefaultId = rs.getString("line_default_id"),
                                kodeLineDefault = rs.getString("kode_line_default"),
                                namaLineDefault = rs.getString("nama_line_default"),
                                aktif = rs.getInt("aktif") == 1,
                                sumberData = rs.getString("sumber_data")
                            )
                        )
                    }
                }
                HasilOperasi.Berhasil(daftar)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca daftar part: ${e.message}"))
        }
    }

    fun bacaDaftarJenisDefect(kataKunci: String = ""): HasilOperasi<List<JenisDefect>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = if (kataKunci.isBlank()) {
                    "SELECT * FROM master_jenis_defect ORDER BY kode_defect"
                } else {
                    "SELECT * FROM master_jenis_defect WHERE nama_defect LIKE ? OR kode_defect LIKE ? ORDER BY kode_defect"
                }
                val daftar = mutableListOf<JenisDefect>()
                koneksi.prepareStatement(sql).use { ps ->
                    if (kataKunci.isNotBlank()) {
                        val pola = "%$kataKunci%"
                        ps.setString(1, pola)
                        ps.setString(2, pola)
                    }
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftar.add(
                            JenisDefect(
                                id = rs.getString("id"),
                                kodeDefect = rs.getString("kode_defect"),
                                namaDefect = rs.getString("nama_defect"),
                                kategoriDefectId = rs.getString("kategori_defect_id"),
                                kodeKategori = rs.getString("kode_kategori"),
                                namaKategori = rs.getString("nama_kategori"),
                                aktif = rs.getInt("aktif") == 1
                            )
                        )
                    }
                }
                HasilOperasi.Berhasil(daftar)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca daftar jenis defect: ${e.message}"))
        }
    }

    fun bacaDaftarMaterial(kataKunci: String = ""): HasilOperasi<List<Material>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = if (kataKunci.isBlank()) {
                    "SELECT * FROM master_material ORDER BY nama_material"
                } else {
                    "SELECT * FROM master_material WHERE nama_material LIKE ? OR kode_material LIKE ? ORDER BY nama_material"
                }
                val daftar = mutableListOf<Material>()
                koneksi.prepareStatement(sql).use { ps ->
                    if (kataKunci.isNotBlank()) {
                        val pola = "%$kataKunci%"
                        ps.setString(1, pola)
                        ps.setString(2, pola)
                    }
                    val rs = ps.executeQuery()
                    while (rs.next()) {
                        daftar.add(
                            Material(
                                id = rs.getString("id"),
                                kodeMaterial = rs.getString("kode_material"),
                                namaMaterial = rs.getString("nama_material"),
                                aktif = rs.getInt("aktif") == 1
                            )
                        )
                    }
                }
                HasilOperasi.Berhasil(daftar)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca daftar material: ${e.message}"))
        }
    }

    fun bacaDaftarSlotWaktu(): HasilOperasi<List<SlotWaktu>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val daftar = mutableListOf<SlotWaktu>()
                koneksi.createStatement().use { st ->
                    val rs = st.executeQuery("SELECT * FROM master_slot_waktu ORDER BY urutan_tampil")
                    while (rs.next()) {
                        daftar.add(
                            SlotWaktu(
                                id = rs.getString("id"),
                                kodeSlot = rs.getString("kode_slot"),
                                labelSlot = rs.getString("label_slot"),
                                jamMulai = rs.getString("jam_mulai"),
                                jamSelesai = rs.getString("jam_selesai"),
                                aktif = rs.getInt("aktif") == 1,
                                urutanTampil = rs.getInt("urutan_tampil")
                            )
                        )
                    }
                }
                HasilOperasi.Berhasil(daftar)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca daftar slot waktu: ${e.message}"))
        }
    }

    fun bacaDaftarLineProduksi(): HasilOperasi<List<LineProduksi>> {
        return try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val daftar = mutableListOf<LineProduksi>()
                koneksi.createStatement().use { st ->
                    val rs = st.executeQuery("SELECT * FROM master_line_produksi ORDER BY urutan_tampil")
                    while (rs.next()) {
                        daftar.add(
                            LineProduksi(
                                id = rs.getString("id"),
                                kodeLine = rs.getString("kode_line"),
                                namaLine = rs.getString("nama_line"),
                                aktif = rs.getInt("aktif") == 1,
                                urutanTampil = rs.getInt("urutan_tampil")
                            )
                        )
                    }
                }
                HasilOperasi.Berhasil(daftar)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Gagal membaca daftar line produksi: ${e.message}"))
        }
    }
}
