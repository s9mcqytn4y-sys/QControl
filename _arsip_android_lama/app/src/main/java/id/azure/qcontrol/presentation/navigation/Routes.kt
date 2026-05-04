package id.azure.qcontrol.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data object InspeksiHarian : Route

    @Serializable
    data object DataDefect : Route
}
