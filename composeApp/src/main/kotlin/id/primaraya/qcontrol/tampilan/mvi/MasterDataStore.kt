package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.mvi.MviStore
import id.primaraya.qcontrol.ranah.usecase.*
import id.primaraya.qcontrol.tampilan.state.TabMasterData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MasterDataStore(
    private val tarikMasterDataUseCase: TarikMasterDataQControlUseCase,
    private val bacaRingkasanMasterDataUseCase: BacaRingkasanMasterDataUseCase,
    private val bacaDaftarPartMasterUseCase: BacaDaftarPartMasterUseCase,
    private val bacaDaftarJenisDefectMasterUseCase: BacaDaftarJenisDefectMasterUseCase,
    private val bacaDaftarMaterialMasterUseCase: BacaDaftarMaterialMasterUseCase,
    private val bacaDaftarSlotWaktuMasterUseCase: BacaDaftarSlotWaktuMasterUseCase,
    private val bacaDaftarLineProduksiMasterUseCase: BacaDaftarLineProduksiMasterUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MviStore<MasterDataIntent, MasterDataState, MasterDataEffect> {

    private val _state = MutableStateFlow(MasterDataState())
    override val state: StateFlow<MasterDataState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<MasterDataEffect?>(null)
    override val effect: StateFlow<MasterDataEffect?> = _effect.asStateFlow()

    private val _kataKunciFlow = MutableStateFlow("")

    init {
        _kataKunciFlow
            .debounce(300)
            .onEach { muatDataSesuaiTab() }
            .launchIn(scope)
    }

    override fun tangani(intent: MasterDataIntent) {
        when (intent) {
            is MasterDataIntent.TarikDariServer -> tarikDariServer()
            is MasterDataIntent.MuatLokal -> muatLokal()
            is MasterDataIntent.PilihTab -> {
                _state.update { it.copy(tabAktif = intent.tab) }
                muatDataSesuaiTab()
            }
            is MasterDataIntent.Cari -> {
                _state.update { it.copy(kataKunci = intent.kataKunci) }
                _kataKunciFlow.value = intent.kataKunci
            }
        }
    }

    override fun bersihkanEffect() {
        _effect.value = null
    }

    private fun tarikDariServer() {
        _state.update { it.copy(sedangMemuat = true, pesan = "Menarik master data dari server...") }
        scope.launch {
            when (val hasil = tarikMasterDataUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    _state.update { it.copy(sedangMemuat = false) }
                    _effect.emit(MasterDataEffect.TampilkanPesan("Master data berhasil ditarik", true))
                    muatLokal()
                }
                is HasilOperasi.Gagal -> {
                    _state.update { it.copy(sedangMemuat = false, pesan = hasil.kesalahan.pesan) }
                    _effect.emit(MasterDataEffect.TampilkanPesan("Gagal tarik data: ${hasil.kesalahan.pesan}", false))
                }
            }
        }
    }

    private fun muatLokal() {
        scope.launch {
            when (val hasil = bacaRingkasanMasterDataUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _state.update { it.copy(ringkasan = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanMasterData) }
                    muatDataSesuaiTab()
                }
                is HasilOperasi.Gagal -> {}
            }
        }
    }

    private fun muatDataSesuaiTab() {
        val tab = _state.value.tabAktif
        val kataKunci = _state.value.kataKunci
        scope.launch {
            when (tab) {
                TabMasterData.PART -> {
                    val hasil = bacaDaftarPartMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _state.update { it.copy(daftarPart = hasil.data as List<id.primaraya.qcontrol.ranah.model.Part>) }
                    }
                }
                TabMasterData.JENIS_DEFECT -> {
                    val hasil = bacaDaftarJenisDefectMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _state.update { it.copy(daftarJenisDefect = hasil.data as List<id.primaraya.qcontrol.ranah.model.JenisDefect>) }
                    }
                }
                TabMasterData.MATERIAL -> {
                    val hasil = bacaDaftarMaterialMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _state.update { it.copy(daftarMaterial = hasil.data as List<id.primaraya.qcontrol.ranah.model.Material>) }
                    }
                }
                TabMasterData.SLOT_WAKTU -> {
                    val hasil = bacaDaftarSlotWaktuMasterUseCase.eksekusi()
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _state.update { it.copy(daftarSlotWaktu = hasil.data as List<id.primaraya.qcontrol.ranah.model.SlotWaktu>) }
                    }
                }
                TabMasterData.LINE_PRODUKSI -> {
                    val hasil = bacaDaftarLineProduksiMasterUseCase.eksekusi()
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _state.update { it.copy(daftarLineProduksi = hasil.data as List<id.primaraya.qcontrol.ranah.model.LineProduksi>) }
                    }
                }
                TabMasterData.RINGKASAN -> {}
            }
        }
    }
}
