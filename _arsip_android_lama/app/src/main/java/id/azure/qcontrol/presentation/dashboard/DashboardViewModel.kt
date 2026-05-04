package id.azure.qcontrol.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.azure.qcontrol.domain.repository_interface.QCRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: QCRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect: SharedFlow<DashboardEffect> = _effect.asSharedFlow()

    init {
        handleIntent(DashboardIntent.LoadDashboardData)
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadData()
            is DashboardIntent.RefreshData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getLatestInspeksi()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effect.emit(DashboardEffect.ShowError(e.message ?: "Terjadi kesalahan"))
                }
                .collect { inspeksi ->
                    if (inspeksi != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                totalCheck = inspeksi.totalCheck,
                                totalDefect = inspeksi.totalDefect,
                                rasioNg = inspeksi.rasioNg,
                                error = null
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
        }
    }
}
