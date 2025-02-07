package com.hashem.opendictionary.feature.ui

import androidx.compose.runtime.Stable

@Stable
sealed interface AppUIState {
    data object Loading : AppUIState
    data object Success : AppUIState
    data class Error(val message: String) : AppUIState
}
