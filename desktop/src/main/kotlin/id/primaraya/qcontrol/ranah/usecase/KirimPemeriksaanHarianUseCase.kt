package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriInputHarianLokal
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.data.remote.dto.*
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

class KirimPemeriksaanHarianUseCase(
    private val repositoriLokal: RepositoriInputHarianLokal,
    private val repositoriOutbox: RepositoriOutboxSinkronisasi
) {
    suspend fun eksekusi(draft: DraftPemeriksaanHarian): HasilOperasi<String> {
        // 1. Ambil data lengkap dari SQLite
        val hasilParts = repositoriLokal.ambilDraftInputPart(draft.id)
        if (hasilParts !is HasilOperasi.Berhasil) return HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal mengambil data part draft"))
        
        val parts = hasilParts.data
        
        val hasilTanpaNg = repositoriLokal.ambilDraftProduksiTanpaNg(draft.id)
        val produksiTanpaNg = if (hasilTanpaNg is HasilOperasi.Berhasil) hasilTanpaNg.data else emptyList()

        if (parts.isEmpty() && produksiTanpaNg.isEmpty()) {
            return HasilOperasi.Gagal(KesalahanAplikasi.Validasi("Belum ada data (Part atau Produksi Tanpa NG) yang diinput."))
        }

        // 2. Bangun DTO Payload
        val daftarPartDto = parts.map { part ->
            val hasilDefects = repositoriLokal.ambilDraftInputDefectSlot(part.id)
            val defects = if (hasilDefects is HasilOperasi.Berhasil) hasilDefects.data else emptyList()
            
            PermintaanSimpanPemeriksaanPartDto(
                partId = part.partId,
                totalCheck = part.qtyCheck,
                daftarDefect = defects.map { defect ->
                    PermintaanSimpanPemeriksaanDefectDto(
                        relasiPartDefectId = defect.relasiPartDefectId,
                        slotWaktuId = defect.slotWaktuId,
                        jumlahDefect = defect.jumlahDefect
                    )
                }.filter { it.jumlahDefect > 0 }
            )
        }.filter { it.totalCheck > 0 }

        val daftarProduksiTanpaNgDto = produksiTanpaNg.map { item ->
            PermintaanSimpanProduksiTanpaNgDto(
                partId = item.partId,
                totalProduksi = item.totalProduksi,
                catatan = item.catatan
            )
        }.filter { it.totalProduksi > 0 }

        if (daftarPartDto.isEmpty() && daftarProduksiTanpaNgDto.isEmpty()) {
            return HasilOperasi.Gagal(KesalahanAplikasi.Validasi("Tidak ada data valid (Check > 0 atau Produksi > 0) untuk dikirim."))
        }

        val payloadDto = PermintaanSimpanPemeriksaanHarianDto(
            clientDraftId = draft.clientDraftId,
            tanggalProduksi = draft.tanggalProduksi,
            lineProduksiId = draft.lineId,
            nomorDokumen = draft.nomorDokumen,
            revisi = draft.revisi,
            catatan = draft.catatan,
            daftarPart = daftarPartDto,
            daftarProduksiTanpaNg = daftarProduksiTanpaNgDto
        )
        
        println("[DEBUG] Submit Harian: Tanggal=${draft.tanggalProduksi}, LineID=${draft.lineId}, Parts=${daftarPartDto.size}")

        val payloadJson = Json.encodeToString(payloadDto)
        val hashPayload = hitungHash(payloadJson)

        // 3. Cek apakah payload berubah (opsional, tapi bagus untuk efisiensi)
        // if (draft.hashPayload == hashPayload && draft.statusDraft == "TERKIRIM") {
        //    return HasilOperasi.Berhasil("Payload sama dengan yang sudah terkirim.")
        // }

        // 4. Masukkan ke Outbox
        val itemOutbox = ItemOutboxSinkronisasi(
            id = UUID.randomUUID().toString(),
            jenisOperasi = "SIMPAN_PEMERIKSAAN_HARIAN",
            endpointTujuan = "/api/v1/qcontrol/pemeriksaan-harian",
            metodeHttp = MetodeHttpSinkronisasi.POST,
            payloadJson = payloadJson,
            idempotencyKey = draft.idempotencyKey,
            hashPayload = hashPayload,
            status = StatusOutboxSinkronisasi.MENUNGGU,
            jumlahPercobaan = 0,
            pesanGagalTerakhir = null,
            dibuatPada = Instant.now().toString(),
            diperbaruiPada = Instant.now().toString(),
            dikirimPada = null
        )

        val hasilTambah = repositoriOutbox.tambah(itemOutbox)
        if (hasilTambah is HasilOperasi.Gagal) return hasilTambah

        // 5. Update Status Draft Lokal
        val hasilUpdate = repositoriLokal.updateStatusDraft(draft.id, "SEDANG_DIKIRIM", hashPayload)
        if (hasilUpdate is HasilOperasi.Gagal) return HasilOperasi.Gagal(hasilUpdate.kesalahan)
        
        return HasilOperasi.Berhasil(itemOutbox.id)
    }

    private fun hitungHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
