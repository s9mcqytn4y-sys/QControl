package id.azure.qcontrol.presentation.defect

import id.azure.qcontrol.domain.model.DataDefect

sealed interface DataDefectIntent {
    data object LoadData : DataDefectIntent
    data class AddDefect(val jenis: String, val jumlah: Int, val area: String) : DataDefectIntent
}

data class DataDefectState(
    val isLoading: Boolean = false,
    val items: List<DataDefect> = emptyList(),
    val error: String? = null
)

sealed interface DataDefectEffect {
    data class ShowError(val message: String) : DataDefectEffect
    data object SaveSuccess : DataDefectEffect
}
