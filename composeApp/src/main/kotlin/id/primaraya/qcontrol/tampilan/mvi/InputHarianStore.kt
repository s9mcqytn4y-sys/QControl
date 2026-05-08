package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.mvi.MviStore
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.ranah.usecase.*
import id.primaraya.qcontrol.tampilan.state.TipePesanFlash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class InputHarianStore(
    private val kelolaInputHarianUseCase: KelolaInputHarianUseCase,
    private val kirimPemeriksaanHarianUseCase: KirimPemeriksaanHarianUseCase,
    private val bacaDaftarSlotWaktuMasterUseCase: BacaDaftarSlotWaktuMasterUseCase,
    private val bacaTemplateDefectPartUseCase: BacaTemplateDefectPartUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MviStore<InputHarianIntent, InputHarianState, InputHarianEffect> {

    private val _state = MutableStateFlow(InputHarianState())
    override val state: StateFlow<InputHarianState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<InputHarianEffect?>(null)
    override val effect: StateFlow<InputHarianEffect?> = _effect.asStateFlow()

    private val _kataKunciFlow = MutableStateFlow("")

    init {
        _kataKunciFlow
            .debounce(300)
            .onEach { muatDaftarPart() }
            .launchIn(scope)
    }

    override fun tangani(intent: InputHarianIntent) {
        when (intent) {
            is InputHarianIntent.Inisialisasi -> {
                _state.update { it.copy(tanggal = intent.tanggal, lineId = intent.lineId) }
                muatDraft()
            }
            is InputHarianIntent.GantiLine -> {
                _state.update { it.copy(lineId = intent.lineId, partTerpilih = null, matrixDefect = null) }
                muatDraft()
            }
            is InputHarianIntent.GantiTanggal -> {
                _state.update { it.copy(tanggal = intent.tanggal, partTerpilih = null, matrixDefect = null) }
                muatDraft()
            }
            is InputHarianIntent.CariPart -> {
                _state.update { it.copy(kataKunciPart = intent.kataKunci) }
                _kataKunciFlow.value = intent.kataKunci
            }
            is InputHarianIntent.PilihPart -> {
                _state.update { it.copy(partTerpilih = intent.part, matrixDefect = null, pesanValidasi = null) }
                if (intent.part != null) muatMatrixDefect(intent.part)
            }
            is InputHarianIntent.UpdateQtyCheck -> updateQtyCheck(intent.partId, intent.qty)
            is InputHarianIntent.UpdateDefect -> updateDefect(intent.partId, intent.slotId, intent.relasiId, intent.qty)
            is InputHarianIntent.UpdateProduksiTanpaNg -> updateProduksiTanpaNg(intent.partId, intent.qty)
            is InputHarianIntent.KirimKeServer -> kirimKeServer()
            is InputHarianIntent.ResetDraft -> resetDraft()
        }
    }

    override fun bersihkanEffect() {
        _effect.value = null
    }

    private fun muatDraft() {
        val tgl = _state.value.tanggal
        val line = _state.value.lineId
        if (tgl.isEmpty() || line.isEmpty()) return

        _state.update { it.copy(sedangMemuat = true) }
        scope.launch {
            when (val hasil = kelolaInputHarianUseCase.ambilAtauBuatDraft(tgl, line)) {
                is HasilOperasi.Berhasil<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val draft = hasil.data as DraftPemeriksaanHarian
                    _state.update { 
                        it.copy(
                            sedangMemuat = false,
                            draft = draft
                        ) 
                    }
                    muatDaftarPart()
                    muatDaftarProduksiTanpaNg()
                    muatRingkasan()
                }
                is HasilOperasi.Gagal -> {
                    _state.update { it.copy(sedangMemuat = false) }
                    _effect.emit(InputHarianEffect.TampilkanPesan("Gagal muat draft: ${hasil.kesalahan.pesan}", TipePesanFlash.ERROR))
                }
            }
        }
    }

    private fun muatDaftarPart() {
        val draftId = _state.value.draft?.id ?: return
        val lineId = _state.value.lineId
        val query = _state.value.kataKunciPart
        scope.launch {
            val hasil = kelolaInputHarianUseCase.bacaDaftarPart(draftId, lineId, query)
            if (hasil is HasilOperasi.Berhasil<*>) {
                @Suppress("UNCHECKED_CAST")
                _state.update { it.copy(daftarPart = hasil.data as List<DraftInputPart>) }
            }
        }
    }

    private fun muatDaftarProduksiTanpaNg() {
        val draftId = _state.value.draft?.id ?: return
        scope.launch {
            val hasil = kelolaInputHarianUseCase.bacaDaftarProduksiTanpaNg(draftId)
            if (hasil is HasilOperasi.Berhasil<*>) {
                @Suppress("UNCHECKED_CAST")
                _state.update { it.copy(daftarProduksiTanpaNg = hasil.data as List<DraftProduksiTanpaNg>) }
            }
        }
    }

    private fun muatRingkasan() {
        val draftId = _state.value.draft?.id ?: return
        scope.launch {
            val hasil = kelolaInputHarianUseCase.hitungRingkasan(draftId)
            if (hasil is HasilOperasi.Berhasil<*>) {
                @Suppress("UNCHECKED_CAST")
                _state.update { it.copy(ringkasan = hasil.data as RingkasanInputHarian) }
            }
        }
    }

    private fun muatMatrixDefect(part: DraftInputPart) {
        scope.launch {
            val hasilSlot = bacaDaftarSlotWaktuMasterUseCase.eksekusi()
            if (hasilSlot !is HasilOperasi.Berhasil<*>) return@launch
            @Suppress("UNCHECKED_CAST")
            val slots = hasilSlot.data as List<SlotWaktu>

            val hasilTemplate = bacaTemplateDefectPartUseCase.eksekusi(part.partId)
            if (hasilTemplate !is HasilOperasi.Berhasil<*>) return@launch
            @Suppress("UNCHECKED_CAST")
            val templates = hasilTemplate.data as List<TemplateDefectPart>

            val hasilInput = kelolaInputHarianUseCase.bacaDaftarDefectSlot(part.id)
            val inputs = if (hasilInput is HasilOperasi.Berhasil<*>) {
                @Suppress("UNCHECKED_CAST")
                hasilInput.data as List<DraftInputDefectSlot>
            } else emptyList()

            val matrix = kelolaInputHarianUseCase.bacaMatrixInputDefect(part, slots, templates, inputs)
            _state.update { it.copy(matrixDefect = matrix) }
        }
    }

    private fun updateQtyCheck(partId: String, qty: Int) {
        if (qty < 0) return
        val harianId = _state.value.draft?.id ?: return
        scope.launch {
            kelolaInputHarianUseCase.updateQtyCheck(harianId, partId, qty)
            muatDaftarPart()
            muatRingkasan()
            // Update selection if needed
            if (_state.value.partTerpilih?.partId == partId) {
                _state.value.daftarPart.find { it.partId == partId }?.let { muatMatrixDefect(it) }
            }
        }
    }

    private fun updateDefect(partId: String, slotId: String, relasiId: String, qty: Int) {
        if (qty < 0) return
        val harianId = _state.value.draft?.id ?: return
        val currentPart = _state.value.partTerpilih ?: return
        
        // Validation logic from original code
        val matrix = _state.value.matrixDefect
        if (matrix != null) {
            val oldVal = matrix.barisDefect.find { it.relasiPartDefectId == relasiId }
                ?.nilaiPerSlot?.find { it.slotWaktuId == slotId }?.jumlahDefect ?: 0
            val diff = qty - oldVal
            if (matrix.ringkasan.totalDefectPart + diff > currentPart.qtyCheck) {
                _state.update { it.copy(pesanValidasi = "Total Defect tidak boleh melebihi Total Check (${currentPart.qtyCheck})") }
                return
            }
        }

        _state.update { it.copy(pesanValidasi = null) }
        scope.launch {
            kelolaInputHarianUseCase.updateDefectSlot(harianId, partId, relasiId, slotId, qty)
            muatDaftarPart()
            muatRingkasan()
            _state.value.daftarPart.find { it.partId == partId }?.let { 
                _state.update { s -> s.copy(partTerpilih = it) }
                muatMatrixDefect(it) 
            }
        }
    }

    private fun updateProduksiTanpaNg(partId: String, qty: Int) {
        if (qty < 0) return
        val harianId = _state.value.draft?.id ?: return
        scope.launch {
            kelolaInputHarianUseCase.updateProduksiTanpaNg(harianId, partId, qty)
            muatDaftarProduksiTanpaNg()
        }
    }

    private fun resetDraft() {
        val draftId = _state.value.draft?.id ?: return
        scope.launch {
            val hasil = kelolaInputHarianUseCase.resetDraft(draftId)
            if (hasil is HasilOperasi.Berhasil<*>) {
                _state.update { it.copy(partTerpilih = null, matrixDefect = null) }
                muatDaftarPart()
                muatRingkasan()
                _effect.emit(InputHarianEffect.TampilkanPesan("Draft berhasil dikosongkan", TipePesanFlash.INFO))
            }
        }
    }

    private fun kirimKeServer() {
        val draft = _state.value.draft ?: return
        scope.launch {
            when (val hasil = kirimPemeriksaanHarianUseCase.eksekusi(draft)) {
                is HasilOperasi.Berhasil<*> -> {
                    _effect.emit(InputHarianEffect.TampilkanPesan("Draft masuk antrean sinkronisasi", TipePesanFlash.SUKSES))
                    muatDraft()
                }
                is HasilOperasi.Gagal -> {
                    _effect.emit(InputHarianEffect.TampilkanPesan("Gagal mengirim: ${hasil.kesalahan.pesan}", TipePesanFlash.ERROR))
                }
            }
        }
    }
}
