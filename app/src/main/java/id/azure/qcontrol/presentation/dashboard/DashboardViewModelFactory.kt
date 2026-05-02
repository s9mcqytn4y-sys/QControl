package id.azure.qcontrol.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import id.azure.qcontrol.QControlApplication
import id.azure.qcontrol.presentation.defect.DataDefectViewModel
import id.azure.qcontrol.presentation.inspection.InspeksiHarianViewModel

object DashboardViewModelFactory {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as QControlApplication
            val repository = application.container.qcRepository
            
            return when {
                modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                    DashboardViewModel(repository) as T
                }
                modelClass.isAssignableFrom(InspeksiHarianViewModel::class.java) -> {
                    InspeksiHarianViewModel(repository) as T
                }
                modelClass.isAssignableFrom(DataDefectViewModel::class.java) -> {
                    DataDefectViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
