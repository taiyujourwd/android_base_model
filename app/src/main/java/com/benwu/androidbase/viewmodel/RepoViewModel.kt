package com.benwu.androidbase.viewmodel

import androidx.lifecycle.viewModelScope
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.repository.RepoRepository
import com.benwu.baselib.api.ApiState
import com.benwu.baselib.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepoSharedFlow(
    val timestamp: Long = 0L,
    val repoList: List<Repo.Item>? = null
)

@HiltViewModel
class RepoViewModel @Inject constructor(private val repository: RepoRepository) : BaseViewModel() {

    private val _repoSharedFlow = MutableSharedFlow<RepoSharedFlow>()
    val repoSharedFlow = _repoSharedFlow.asSharedFlow()

    fun getRepoList(perPage: Int = 50) {
        viewModelScope.launch {
            updateLoading(+1)

            when (val result = repository.getRepoList(perPage)) {
                is ApiState.Success -> {
                    _repoSharedFlow.emit(
                        RepoSharedFlow(
                            timestamp = System.currentTimeMillis(),
                            repoList = result.data.items
                        )
                    )
                }

                is ApiState.Failure -> {
                    updateError(+1)
                }
            }

            updateLoading(-1)
        }
    }
}