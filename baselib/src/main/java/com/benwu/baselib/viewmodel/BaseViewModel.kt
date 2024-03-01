package com.benwu.baselib.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BaseUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

open class BaseViewModel : ViewModel() {

    private val _baseUiState = MutableStateFlow(BaseUiState())
    val baseUiState = _baseUiState.asStateFlow()

    fun updateLoading(isLoading: Boolean) = viewModelScope.launch {
        _baseUiState.update { it.copy(isLoading = isLoading, isError = false) }
    }

    fun updateError(isError: Boolean) = viewModelScope.launch {
        _baseUiState.update { it.copy(isLoading = false, isError = isError) }
    }
}