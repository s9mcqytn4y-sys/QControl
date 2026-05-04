package id.azure.qcontrol.presentation.inspection

import id.azure.qcontrol.domain.model.InspeksiHarian

sealed interface InspeksiHarianIntent {
    data object LoadData : InspeksiHarianIntent
    data class AddInspeksi(val totalCheck: Int, val totalDefect: Int) : InspeksiHarianIntent
}

data class InspeksiHarianState(
    val isLoading: Boolean = false,
    val items: List<InspeksiHarian> = emptyList(),
    val error: String? = null
)

sealed interface InspeksiHarianEffect {
    data class ShowError(val message: String) : InspeksiHarianEffect
    data object SaveSuccess : InspeksiHarianEffect
}
