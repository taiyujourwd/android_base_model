package com.benwu.androidbase.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.repository.RepoRepository
import com.benwu.baselib.api.ApiState
import com.benwu.baselib.viewmodel.BaseViewModel
import javax.inject.Inject

data class RepoUiState(
    val repoList: List<Repo.Item>? = null,
    val repoPagingData: PagingData<Repo.Item>? = null
)

@HiltViewModel
class RepoViewModel @Inject constructor(private val repository: RepoRepository) : BaseViewModel() {

    private val _repoUiState = MutableStateFlow(RepoUiState())
    val repoUiState = _repoUiState.asStateFlow()

    fun getRepoList() = viewModelScope.launch {
        updateLoading(true)

        when (val result = repository.getRepoList()) {
            is ApiState.Success -> {
                _repoUiState.update { it.copy(repoList = result.data.items) }
            }

            is ApiState.Failure -> {
                updateError(true)
            }
        }

        updateLoading(false)
    }

    @ExperimentalPagingApi
    fun getRepoPagingData() = viewModelScope.launch {
        repository.getRepoPagingData().cachedIn(viewModelScope).collectLatest { data ->
            _repoUiState.update { it.copy(repoPagingData = data) }
        }
    }
}