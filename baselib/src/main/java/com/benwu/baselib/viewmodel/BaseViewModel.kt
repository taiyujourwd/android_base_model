package com.benwu.baselib.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BaseStateFlow(
    val loadingCount: Int = 0,
    val errorCount: Int = 0
) {
    val isLoading = loadingCount > 0
    val isError = !isLoading && errorCount > 0
}

open class BaseViewModel : ViewModel() {

    private val _baseStateFlow = MutableStateFlow(BaseStateFlow())
    val baseStateFlow = _baseStateFlow.asStateFlow()

    fun updateLoading(loadingCount: Int) = viewModelScope.launch {
        _baseStateFlow.update {
            it.copy(
                loadingCount = it.loadingCount + loadingCount,
                errorCount = if (it.loadingCount == 0) 0 else it.errorCount
            )
        }
    }

    fun updateError(errorCount: Int) = viewModelScope.launch {
        _baseStateFlow.update { it.copy(errorCount = it.errorCount + errorCount) }
    }
}