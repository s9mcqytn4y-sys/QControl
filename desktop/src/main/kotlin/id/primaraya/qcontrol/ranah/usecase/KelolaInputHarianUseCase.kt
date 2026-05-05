package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriInputHarianLokal
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriMasterDataLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.*
import java.util.UUID

class KelolaInputHarianUseCase(
    private val repositoriInputHarianLokal: RepositoriInputHarianLokal,
    private val repositoriMasterDataLokal: RepositoriMasterDataLokal
) {
    fun ambilAtauBuatDraft(tanggalProduksi: String, lineId: String): HasilOperasi<DraftPemeriksaanHarian> {
        return repositoriInputHarianLokal.ambilAtauBuatDraft(tanggalProduksi, lineId)
    }

    fun bacaDaftarPart(pemeriksaanHarianId: String, kataKunci: String = ""): HasilOperasi<List<DraftInputPart>> {
        // Ambil draft yang sudah ada
        val hasilDraft = repositoriInputHarianLokal.ambilDraftInputPart(pemeriksaanHarianId)
        if (hasilDraft is HasilOperasi.Gagal) return hasilDraft
        
        val draftEksisting = (hasilDraft as HasilOperasi.Berhasil).data
        
        // Ambil dari master data untuk part yang belum ada di draft
        val hasilMaster = repositoriMasterDataLokal.bacaDaftarPart(kataKunci)
        if (hasilMaster is HasilOperasi.Gagal) return HasilOperasi.Berhasil(draftEksisting)

        val masterParts = (hasilMaster as HasilOperasi.Berhasil).data
        
        // Gabungkan: jika di master ada tapi di draft belum ada, tampilkan sebagai draft kosong
        // Namun untuk fase ini, kita tampilkan yang sudah di-input saja atau semua part?
        // Sesuai target Fase 2E-A: pilih part + daftar part. 
        // Idealnya daftar part kiri menunjukkan semua part master yang difilter kata kunci.
        
        val hasilGabungan = masterParts.map { master ->
            val draft = draftEksisting.find { it.partId == master.id }
            DraftInputPart(
                id = draft?.id ?: "",
                pemeriksaanHarianId = pemeriksaanHarianId,
                partId = master.id,
                namaPart = master.namaPart,
                nomorPart = master.nomorPart ?: "",
                qtyCheck = draft?.qtyCheck ?: 0,
                totalOk = draft?.totalOk ?: 0,
                totalDefect = draft?.totalDefect ?: 0
            )
        }
        
        return HasilOperasi.Berhasil(hasilGabungan)
    }

    suspend fun updateQtyCheck(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> {
        return repositoriInputHarianLokal.simpanAtauPerbaruiPart(pemeriksaanHarianId, partId, qty)
    }

    suspend fun updateDefectSlot(pemeriksaanHarianId: String, partId: String, relasiPartDefectId: String, qty: Int): HasilOperasi<Unit> {
        return repositoriInputHarianLokal.simpanAtauPerbaruiDefect(pemeriksaanHarianId, partId, relasiPartDefectId, qty)
    }

    fun hitungRingkasan(pemeriksaanHarianId: String): HasilOperasi<RingkasanInputHarian> {
        return repositoriInputHarianLokal.hitungRingkasan(pemeriksaanHarianId)
    }
}
