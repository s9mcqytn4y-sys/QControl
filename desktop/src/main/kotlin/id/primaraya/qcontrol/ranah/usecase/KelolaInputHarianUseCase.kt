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

    fun bacaDaftarPart(pemeriksaanHarianId: String, lineId: String, kataKunci: String = ""): HasilOperasi<List<DraftInputPart>> {
        // 1. Ambil draft yang sudah tersimpan di SQLite
        val hasilDraft = repositoriInputHarianLokal.ambilDraftInputPart(pemeriksaanHarianId, kataKunci)
        if (hasilDraft is HasilOperasi.Gagal) return hasilDraft
        
        val draftEksisting = (hasilDraft as HasilOperasi.Berhasil).data
        
        // 2. Ambil referensi part aktif dari master data untuk line ini
        val hasilMaster = repositoriMasterDataLokal.bacaDaftarPart(kataKunci, lineId)
        if (hasilMaster is HasilOperasi.Gagal) return HasilOperasi.Berhasil(draftEksisting)

        val masterParts = (hasilMaster as HasilOperasi.Berhasil).data
        
        // 3. Gabungkan: Tampilkan semua part dari master data. 
        // Jika sudah ada di draft, gunakan datanya. Jika belum, tampilkan sebagai 0.
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
                totalDefect = draft?.totalDefect ?: 0,
                rasioDefect = draft?.rasioDefect ?: 0.0,
                urutanTampil = draft?.urutanTampil ?: 0
            )
        }
        
        return HasilOperasi.Berhasil(hasilGabungan)
    }

    suspend fun updateQtyCheck(pemeriksaanHarianId: String, partId: String, qty: Int): HasilOperasi<Unit> {
        return repositoriInputHarianLokal.updateQtyCheck(pemeriksaanHarianId, partId, qty)
    }

    fun bacaDaftarDefectSlot(inputPartId: String): HasilOperasi<List<DraftInputDefectSlot>> {
        return repositoriInputHarianLokal.ambilDraftInputDefectSlot(inputPartId)
    }

    suspend fun updateDefectSlot(
        pemeriksaanHarianId: String,
        partId: String,
        relasiPartDefectId: String,
        slotWaktuId: String,
        qty: Int
    ): HasilOperasi<Unit> {
        return repositoriInputHarianLokal.updateDefectSlot(
            pemeriksaanHarianId,
            partId,
            relasiPartDefectId,
            slotWaktuId,
            qty
        )
    }

    fun bacaMatrixInputDefect(
        draftInputPart: DraftInputPart,
        daftarSlotWaktu: List<SlotWaktu>,
        daftarTemplateDefect: List<TemplateDefectPart>,
        daftarInputDefectSlot: List<DraftInputDefectSlot>
    ): MatrixInputDefectPart {
        val kolomSlotWaktu = daftarSlotWaktu.filter { it.aktif }
            .sortedBy { it.urutanTampil }
            .map {
                KolomSlotWaktuInput(
                    slotWaktuId = it.id,
                    labelSlot = it.labelSlot,
                    kodeSlot = it.kodeSlot,
                    urutan = it.urutanTampil
                )
            }

        val barisDefect = daftarTemplateDefect.sortedBy { it.urutanTampil }.map { template ->
            val nilaiPerSlot = kolomSlotWaktu.map { kolom ->
                val inputEksisting = daftarInputDefectSlot.find {
                    it.relasiPartDefectId == template.id && it.slotWaktuId == kolom.slotWaktuId
                }
                NilaiInputDefectSlot(kolom.slotWaktuId, inputEksisting?.jumlahDefect ?: 0)
            }
            BarisInputDefect(
                relasiPartDefectId = template.id,
                namaDefect = template.namaDefect ?: "Defect",
                kodeDefect = template.kodeDefect ?: "",
                nilaiPerSlot = nilaiPerSlot,
                subtotal = nilaiPerSlot.sumOf { it.jumlahDefect }
            )
        }

        val totalPerSlot = kolomSlotWaktu.associate { kolom ->
            kolom.slotWaktuId to barisDefect.sumOf { baris ->
                baris.nilaiPerSlot.find { it.slotWaktuId == kolom.slotWaktuId }?.jumlahDefect ?: 0
            }
        }

        val totalDefectPart = barisDefect.sumOf { it.subtotal }
        val totalOkPart = Math.max(0, draftInputPart.qtyCheck - totalDefectPart)
        val rasioDefect = if (draftInputPart.qtyCheck > 0) {
            (totalDefectPart.toDouble() / draftInputPart.qtyCheck.toDouble()) * 100.0
        } else 0.0

        return MatrixInputDefectPart(
            partId = draftInputPart.partId,
            namaPart = draftInputPart.namaPart,
            nomorPart = draftInputPart.nomorPart,
            barisDefect = barisDefect,
            kolomSlotWaktu = kolomSlotWaktu,
            ringkasan = RingkasanSlotWaktuInput(
                totalPerSlot = totalPerSlot,
                totalDefectPart = totalDefectPart,
                totalOkPart = totalOkPart,
                rasioDefect = rasioDefect
            )
        )
    }

    fun hitungRingkasan(pemeriksaanHarianId: String): HasilOperasi<RingkasanInputHarian> {
        return repositoriInputHarianLokal.hitungRingkasan(pemeriksaanHarianId)
    }
    
    fun resetDraft(pemeriksaanHarianId: String): HasilOperasi<Unit> {
        return repositoriInputHarianLokal.hapusDraft(pemeriksaanHarianId)
    }
}
