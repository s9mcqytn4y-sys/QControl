package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class RepositoriMasterDataLokal(
    private val database: QControlDatabase
) {
    private val queries = database.qControlQueries

    suspend fun simpanMasterData(masterData: MasterDataQControl): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val waktuSekarang = Instant.now().toString()

            queries.transaction {
                queries.hapusSemuaRelasiPartDefect()
                queries.hapusSemuaJenisDefect()
                queries.hapusSemuaKategoriDefect()
                queries.hapusSemuaPart()
                queries.hapusSemuaMaterial()
                queries.hapusSemuaSlotWaktu()
                queries.hapusSemuaLine()

                masterData.lineProduksi.forEach {
                    queries.simpanLine(it.id, it.kodeLine, it.namaLine, it.aktif, it.urutanTampil)
                }

                masterData.slotWaktu.forEach {
                    queries.simpanSlotWaktu(it.id, it.kodeSlot, it.labelSlot, it.jamMulai, it.jamSelesai, it.aktif, it.urutanTampil)
                }

                masterData.material.forEach {
                    val kodeMaterial = requireNotNull(it.kodeMaterial) {
                        "Kode material kosong untuk material ${it.id}"
                    }
                    queries.simpanMaterial(it.id, kodeMaterial, it.namaMaterial, it.aktif)
                }

                masterData.part.forEach {
                    queries.simpanPart(
                        it.id, it.kodeUnikPart, it.namaPart, it.nomorPart, it.materialId, it.kodeMaterial, it.namaMaterial,
                        it.kodeProyek, it.jumlahItemPerKanban, it.lineDefaultId, it.kodeLineDefault, it.namaLineDefault,
                        it.aktif, it.sumberData
                    )
                }

                masterData.kategoriDefect.forEach {
                    queries.simpanKategoriDefect(it.id, it.kodeKategori, it.namaKategori, it.aktif, it.urutanTampil)
                }

                masterData.jenisDefect.forEach {
                    queries.simpanJenisDefect(it.id, it.kodeDefect, it.namaDefect, it.kategoriDefectId, it.kodeKategori, it.namaKategori, it.aktif)
                }

                masterData.relasiPartDefect.forEach {
                    queries.simpanRelasiPartDefect(
                        id = it.id, 
                        part_id = it.partId, 
                        jenis_defect_id = it.jenisDefectId, 
                        kode_tampilan_defect = it.kodeTampilanDefect, 
                        urutan_tampil = it.urutanTampil, 
                        aktif = it.aktif
                    )
                }

                queries.simpanMetadata("versiMasterData", masterData.versiMasterData, waktuSekarang)
                queries.simpanMetadata("ditarikPada", waktuSekarang, waktuSekarang)
                queries.simpanMetadata("jumlahLineProduksi", masterData.lineProduksi.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahSlotWaktu", masterData.slotWaktu.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahMaterial", masterData.material.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahPart", masterData.part.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahJenisDefect", masterData.jenisDefect.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahRelasiPartDefect", masterData.relasiPartDefect.size.toString(), waktuSekarang)
                queries.simpanMetadata("jumlahShiftOperasional", "1", waktuSekarang)
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menyimpan master data: ${e.message}"))
        }
    }

    suspend fun bacaRingkasanMasterData(): HasilOperasi<RingkasanMasterData> = withContext(Dispatchers.IO) {
        try {
            val metadata = queries.dapatkanSemuaMetadata().executeAsList().associate { it.kunci to it.nilai }
            if (metadata.isEmpty()) {
                return@withContext HasilOperasi.Gagal(KesalahanAplikasi.DataTidakDitemukan("Master data belum tersedia"))
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
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca ringkasan master data: ${e.message}"))
        }
    }

    suspend fun bacaDaftarPart(kataKunci: String = "", lineIdAtauKode: String? = null): HasilOperasi<List<Part>> = withContext(Dispatchers.IO) {
        try {
            val query = "%$kataKunci%"
            val result = if (lineIdAtauKode == null) {
                if (kataKunci.isEmpty()) {
                    queries.dapatkanSemuaPart().executeAsList()
                } else {
                    queries.cariPart(query, query, query, query, query).executeAsList()
                }
            } else {
                queries.cariPartPerLine(lineIdAtauKode, query).executeAsList()
            }

            HasilOperasi.Berhasil(result.map {
                Part(
                    id = it.id,
                    kodeUnikPart = it.kode_unik_part,
                    namaPart = it.nama_part,
                    nomorPart = it.nomor_part,
                    materialId = it.material_id,
                    kodeMaterial = it.kode_material,
                    namaMaterial = it.nama_material,
                    kodeProyek = it.kode_proyek,
                    jumlahItemPerKanban = it.jumlah_item_per_kanban?.toInt(),
                    lineDefaultId = it.line_default_id,
                    kodeLineDefault = it.kode_line_default,
                    namaLineDefault = it.nama_line_default,
                    aktif = it.aktif,
                    sumberData = it.sumber_data
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca daftar part: ${e.message}"))
        }
    }

    suspend fun bacaDaftarJenisDefect(kataKunci: String = ""): HasilOperasi<List<JenisDefect>> = withContext(Dispatchers.IO) {
        try {
            val query = "%$kataKunci%"
            val result = if (kataKunci.isEmpty()) {
                queries.dapatkanSemuaJenisDefect().executeAsList()
            } else {
                queries.cariJenisDefect(query, query).executeAsList()
            }

            HasilOperasi.Berhasil(result.map {
                JenisDefect(
                    id = it.id,
                    kodeDefect = it.kode_defect,
                    namaDefect = it.nama_defect,
                    kategoriDefectId = it.kategori_defect_id,
                    kodeKategori = it.kode_kategori,
                    namaKategori = it.nama_kategori,
                    aktif = it.aktif
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca daftar jenis defect: ${e.message}"))
        }
    }

    suspend fun bacaDaftarMaterial(kataKunci: String = ""): HasilOperasi<List<Material>> = withContext(Dispatchers.IO) {
        try {
            val query = "%$kataKunci%"
            val result = if (kataKunci.isEmpty()) {
                queries.dapatkanSemuaMaterial().executeAsList()
            } else {
                queries.cariMaterial(query, query).executeAsList()
            }

            HasilOperasi.Berhasil(result.map {
                Material(
                    id = it.id,
                    kodeMaterial = it.kode_material,
                    namaMaterial = it.nama_material,
                    aktif = it.aktif
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca daftar material: ${e.message}"))
        }
    }

    suspend fun bacaDaftarSlotWaktu(): HasilOperasi<List<SlotWaktu>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanSemuaSlotWaktu().executeAsList()
            HasilOperasi.Berhasil(result.map {
                SlotWaktu(
                    id = it.id,
                    kodeSlot = it.kode_slot,
                    labelSlot = it.label_slot,
                    jamMulai = it.jam_mulai,
                    jamSelesai = it.jam_selesai,
                    aktif = it.aktif,
                    urutanTampil = it.urutan_tampil
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca daftar slot waktu: ${e.message}"))
        }
    }

    suspend fun bacaDaftarLineProduksi(): HasilOperasi<List<LineProduksi>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanSemuaLine().executeAsList()
            HasilOperasi.Berhasil(result.map {
                LineProduksi(
                    id = it.id,
                    kodeLine = it.kode_line,
                    namaLine = it.nama_line,
                    aktif = it.aktif,
                    urutanTampil = it.urutan_tampil
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca daftar line produksi: ${e.message}"))
        }
    }

    suspend fun bacaRelasiPartDefect(partId: String): HasilOperasi<List<RelasiPartDefect>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanRelasiPartDefect(partId).executeAsList()
            HasilOperasi.Berhasil(result.map {
                RelasiPartDefect(
                    id = it.id,
                    partId = it.part_id,
                    kodeUnikPart = "", 
                    jenisDefectId = it.jenis_defect_id,
                    kodeDefect = "", 
                    kodeTampilanDefect = it.kode_tampilan_defect,
                    urutanTampil = it.urutan_tampil,
                    aktif = it.aktif
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca relasi part defect: ${e.message}"))
        }
    }

    suspend fun bacaTemplateDefectPart(partId: String): HasilOperasi<List<TemplateDefectPart>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanTemplateDefectPart(partId).executeAsList()
            HasilOperasi.Berhasil(result.map {
                TemplateDefectPart(
                    id = it.id,
                    partId = it.part_id,
                    kodeUnikPart = "", 
                    jenisDefectId = it.jenis_defect_id,
                    kodeTampilanDefect = it.kode_tampilan_defect,
                    kodeDefect = it.kode_defect ?: "",
                    namaDefect = it.nama_defect ?: "",
                    namaKategori = it.nama_kategori ?: "",
                    urutanTampil = it.urutan_tampil,
                    aktif = it.aktif
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca template defect part: ${e.message}"))
        }
    }

    suspend fun bacaDiagnostikMasterData(): Map<String, Int> = withContext(Dispatchers.IO) {
        try {
            val stats = mutableMapOf<String, Int>()
            stats["total_part"] = queries.hitungTotalPart().executeAsOne().toInt()
            stats["total_line"] = queries.hitungTotalLine().executeAsOne().toInt()
            stats["total_slot_waktu"] = queries.hitungTotalSlotWaktu().executeAsOne().toInt()
            stats["total_relasi_part_defect"] = queries.hitungTotalRelasiPartDefect().executeAsOne().toInt()
            
            val lines = queries.dapatkanSemuaLine().executeAsList()
            lines.forEach { line ->
                stats["part_line_${line.nama_line}"] = queries.hitungPartPerLine(line.id, line.id, line.id).executeAsOne().toInt()
            }
            stats
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
