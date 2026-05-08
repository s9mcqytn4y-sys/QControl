package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class RepositoriInputHarianLokal(
    private val database: QControlDatabase
) {
    private val queries = database.qControlQueries

    suspend fun ambilAtauBuatDraft(tanggalProduksi: String, lineId: String): HasilOperasi<DraftPemeriksaanHarian> = withContext(Dispatchers.IO) {
        try {
            val existing = queries.dapatkanDraftHarian(tanggalProduksi, lineId).executeAsOneOrNull()
            if (existing != null) {
                HasilOperasi.Berhasil(
                    DraftPemeriksaanHarian(
                        id = existing.id,
                        clientDraftId = existing.client_draft_id,
                        tanggalProduksi = existing.tanggal_produksi,
                        lineId = existing.line_produksi_id,
                        nomorDokumen = existing.nomor_dokumen,
                        revisi = existing.revisi,
                        catatan = existing.catatan,
                        statusDraft = existing.status_draft,
                        idempotencyKey = existing.idempotency_key,
                        hashPayload = existing.hash_payload,
                        terakhirDisimpanPada = existing.terakhir_disimpan_pada,
                        terakhirDikirimPada = existing.terakhir_dikirim_pada,
                        pesanErrorTerakhir = existing.pesan_error_terakhir,
                        dibuatPada = existing.created_at,
                        diperbaruiPada = existing.updated_at
                    )
                )
            } else {
                val idBaru = UUID.randomUUID().toString()
                val clientDraftId = "DRAFT-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}"
                val idempotencyKey = UUID.randomUUID().toString()
                val waktuSekarang = Instant.now().toString()

                queries.simpanDraftHarian(
                    id = idBaru,
                    client_draft_id = clientDraftId,
                    tanggal_produksi = tanggalProduksi,
                    line_produksi_id = lineId,
                    nomor_dokumen = null,
                    revisi = null,
                    catatan = null,
                    status_draft = "DRAFT",
                    idempotency_key = idempotencyKey,
                    hash_payload = null,
                    terakhir_disimpan_pada = waktuSekarang,
                    terakhir_dikirim_pada = null,
                    pesan_error_terakhir = null,
                    created_at = waktuSekarang,
                    updated_at = waktuSekarang
                )

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
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil/buat draft: ${e.message}"))
        }
    }

    suspend fun ambilDraftInputPart(pemeriksaanHarianId: String, kataKunci: String = ""): HasilOperasi<List<DraftInputPart>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanDaftarPartDraft(pemeriksaanHarianId, "%$kataKunci%", "%$kataKunci%").executeAsList()
            HasilOperasi.Berhasil(result.map {
                DraftInputPart(
                    id = it.id,
                    pemeriksaanHarianId = it.pemeriksaan_harian_draft_id,
                    partId = it.part_id,
                    namaPart = it.nama_part ?: "",
                    nomorPart = it.nomor_part ?: "",
                    qtyCheck = it.total_check,
                    totalOk = it.total_ok,
                    totalDefect = it.total_defect,
                    rasioDefect = it.rasio_defect,
                    urutanTampil = it.urutan_tampil
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft input part: ${e.message}"))
        }
    }

    suspend fun ambilDraftInputDefectSlot(inputPartId: String): HasilOperasi<List<DraftInputDefectSlot>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanDaftarDefectSlotDraft(inputPartId).executeAsList()
            HasilOperasi.Berhasil(result.map {
                DraftInputDefectSlot(
                    id = it.id,
                    inputPartId = it.pemeriksaan_part_draft_id,
                    relasiPartDefectId = it.relasi_part_defect_id,
                    slotWaktuId = it.slot_waktu_id,
                    namaDefect = it.nama_defect ?: "",
                    jumlahDefect = it.jumlah_defect
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft defect slot: ${e.message}"))
        }
    }

    suspend fun ambilDraftProduksiTanpaNg(pemeriksaanHarianId: String): HasilOperasi<List<DraftProduksiTanpaNg>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanDaftarProduksiTanpaNgDraft(pemeriksaanHarianId).executeAsList()
            HasilOperasi.Berhasil(result.map {
                DraftProduksiTanpaNg(
                    partId = it.part_id,
                    namaPart = it.nama_part ?: "",
                    nomorPart = it.nomor_part ?: "",
                    totalProduksi = it.total_produksi,
                    catatan = it.catatan
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal ambil draft produksi tanpa NG: ${e.message}"))
        }
    }

    suspend fun updateProduksiTanpaNg(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val waktuSekarang = Instant.now().toString()
            queries.simpanProduksiTanpaNgDraft(
                id = UUID.randomUUID().toString(),
                pemeriksaan_harian_draft_id = pemeriksaanHarianId,
                part_id = partId,
                total_produksi = qty,
                catatan = null,
                created_at = waktuSekarang,
                updated_at = waktuSekarang
            )
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update produksi tanpa NG: ${e.message}"))
        }
    }

    suspend fun updateQtyCheck(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val waktuSekarang = Instant.now().toString()
            queries.transaction {
                val existingPart = queries.dapatkanPartDraft(pemeriksaanHarianId, partId).executeAsOneOrNull()
                if (existingPart == null) {
                    queries.simpanPartDraft(
                        id = UUID.randomUUID().toString(),
                        pemeriksaan_harian_draft_id = pemeriksaanHarianId,
                        part_id = partId,
                        total_check = qty,
                        total_ok = qty,
                        total_defect = 0,
                        rasio_defect = 0.0,
                        urutan_tampil = 0,
                        created_at = waktuSekarang,
                        updated_at = waktuSekarang
                    )
                } else {
                    queries.updateQtyPartDraft(
                        total_check = qty,
                        updated_at = waktuSekarang,
                        id = existingPart.id
                    )
                }
                queries.updateWaktuSimpanHarian(waktuSekarang, waktuSekarang, pemeriksaanHarianId)
            }
            HasilOperasi.Berhasil(Unit)
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
    ): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val waktuSekarang = Instant.now().toString()
            queries.transaction {
                var partDraft = queries.dapatkanPartDraft(pemeriksaanHarianId, partId).executeAsOneOrNull()
                if (partDraft == null) {
                    val newPartId = UUID.randomUUID().toString()
                    queries.simpanPartDraft(
                        id = newPartId,
                        pemeriksaan_harian_draft_id = pemeriksaanHarianId,
                        part_id = partId,
                        total_check = 0,
                        total_ok = 0,
                        total_defect = 0,
                        rasio_defect = 0.0,
                        urutan_tampil = 0,
                        created_at = waktuSekarang,
                        updated_at = waktuSekarang
                    )
                    partDraft = queries.dapatkanPartDraft(pemeriksaanHarianId, partId).executeAsOneOrNull()
                }

                if (partDraft != null) {
                    queries.simpanDefectSlotDraft(
                        id = UUID.randomUUID().toString(),
                        pemeriksaan_part_draft_id = partDraft.id,
                        relasi_part_defect_id = relasiPartDefectId,
                        slot_waktu_id = slotWaktuId,
                        jumlah_defect = qty,
                        created_at = waktuSekarang,
                        updated_at = waktuSekarang
                    )
                    queries.updateTotalDefectPartDraft(partDraft.id, waktuSekarang, partDraft.id)
                    queries.updateFinalKalkulasiPartDraft(partDraft.id)
                }
                queries.updateWaktuSimpanHarian(waktuSekarang, waktuSekarang, pemeriksaanHarianId)
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update defect slot: ${e.message}"))
        }
    }

    suspend fun hitungRingkasan(pemeriksaanHarianId: String): HasilOperasi<RingkasanInputHarian> = withContext(Dispatchers.IO) {
        try {
            val harian = queries.dapatkanDraftHarianById(pemeriksaanHarianId).executeAsOneOrNull()
            val lineId = harian?.line_produksi_id ?: ""

            val partSudahDiisi = queries.hitungPartSudahDiisi(pemeriksaanHarianId).executeAsOne().toInt()
            
            // Hitung total part master for this line
            // Need a query for this in QControl.sq
            val totalPartMaster = queries.hitungTotalPartPerLine(lineId, lineId, lineId).executeAsOne().toInt()

            val totals = queries.hitungTotalQtyHarian(pemeriksaanHarianId).executeAsOne()
            val totalQtyCheck = totals.t_check?.toInt() ?: 0
            val totalQtyDefect = totals.t_defect?.toInt() ?: 0

            val defects = queries.hitungDefectPerJenisHarian(pemeriksaanHarianId).executeAsList()
            val daftarDefect = defects.map { DefectTerhitung(it.nama_defect ?: "", it.jumlah?.toInt() ?: 0) }

            val slots = queries.hitungDefectPerSlotHarian(pemeriksaanHarianId).executeAsList()
            val daftarPerSlot = slots.map { DefectPerSlotTerhitung(it.id, it.label_slot, it.jumlah?.toInt() ?: 0) }

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
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal hitung ringkasan: ${e.message}"))
        }
    }

    suspend fun hapusDraft(pemeriksaanHarianId: String): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.transaction {
                // Cascading delete is handled by Foreign Key in SQLDelight if enabled
                // But let's be explicit if needed or trust the Schema
                queries.hapusDraftHarian(pemeriksaanHarianId)
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal hapus draft: ${e.message}"))
        }
    }

    suspend fun updateStatusDraft(id: String, status: String, hash: String?): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.updateStatusDraft(status, hash, Instant.now().toString(), id)
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update status draft: ${e.message}"))
        }
    }
}
