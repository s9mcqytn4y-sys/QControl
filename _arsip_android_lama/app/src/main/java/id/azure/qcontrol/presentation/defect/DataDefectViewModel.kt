package id.azure.qcontrol.presentation.defect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.azure.qcontrol.domain.model.DataDefect
import id.azure.qcontrol.domain.repository_interface.QCRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class DataDefectViewModel(
    private val repository: QCRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DataDefectState())
    val state: StateFlow<DataDefectState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DataDefectEffect>()
    val effect: SharedFlow<DataDefectEffect> = _effect.asSharedFlow()

    init {
        handleIntent(DataDefectIntent.LoadData)
    }

    fun handleIntent(intent: DataDefectIntent) {
        when (intent) {
            is DataDefectIntent.LoadData -> loadData()
            is DataDefectIntent.AddDefect -> addDefect(intent.jenis, intent.jumlah, intent.area)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getDefectHistory()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effect.emit(DataDefectEffect.ShowError(e.message ?: "Gagal memuat data"))
                }
                .collect { items ->
                    _state.update { it.copy(isLoading = false, items = items, error = null) }
                }
        }
    }

    private fun addDefect(jenis: String, jumlah: Int, area: String) {
        viewModelScope.launch {
            val newItem = DataDefect(
                id = UUID.randomUUID().toString(),
                jenisDefect = jenis,
                jumlah = jumlah,
                area = area,
                inspeksiId = "1" // Default for now
            )
            repository.addDefect(newItem)
            _effect.emit(DataDefectEffect.SaveSuccess)
            loadData()
        }
    }
}
