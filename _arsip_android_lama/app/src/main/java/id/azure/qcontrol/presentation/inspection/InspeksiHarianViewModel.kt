package id.azure.qcontrol.presentation.inspection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.azure.qcontrol.domain.model.InspeksiHarian
import id.azure.qcontrol.domain.repository_interface.QCRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class InspeksiHarianViewModel(
    private val repository: QCRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InspeksiHarianState())
    val state: StateFlow<InspeksiHarianState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<InspeksiHarianEffect>()
    val effect: SharedFlow<InspeksiHarianEffect> = _effect.asSharedFlow()

    init {
        handleIntent(InspeksiHarianIntent.LoadData)
    }

    fun handleIntent(intent: InspeksiHarianIntent) {
        when (intent) {
            is InspeksiHarianIntent.LoadData -> loadData()
            is InspeksiHarianIntent.AddInspeksi -> addInspeksi(intent.totalCheck, intent.totalDefect)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllInspeksi()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effect.emit(InspeksiHarianEffect.ShowError(e.message ?: "Gagal memuat data"))
                }
                .collect { items ->
                    _state.update { it.copy(isLoading = false, items = items, error = null) }
                }
        }
    }

    private fun addInspeksi(totalCheck: Int, totalDefect: Int) {
        viewModelScope.launch {
            val rasioNg = if (totalCheck > 0) (totalDefect.toFloat() / totalCheck) * 100 else 0f
            val newItem = InspeksiHarian(
                id = UUID.randomUUID().toString(),
                totalCheck = totalCheck,
                totalDefect = totalDefect,
                rasioNg = rasioNg,
                timestamp = System.currentTimeMillis()
            )
            repository.addInspeksi(newItem)
            _effect.emit(InspeksiHarianEffect.SaveSuccess)
            loadData()
        }
    }
}
