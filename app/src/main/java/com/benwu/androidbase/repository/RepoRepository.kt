package com.benwu.androidbase.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.benwu.androidbase.api.ApiService
import com.benwu.androidbase.db.DemoDatabase
import com.benwu.androidbase.paging.RepoRemoteMediator
import com.benwu.baselib.extension.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: DemoDatabase
) {
    suspend fun getRepoList() = safeApiCall {
        apiService.getRepoList(1, 50)
    }

//    fun getRepoPagingData() = Pager(config = PagingConfig(50)) {
//        RepoPagingSource(apiService)
//    }.flow

    @ExperimentalPagingApi
    fun getRepoPagingData() = Pager(
        config = PagingConfig(50),
        remoteMediator = RepoRemoteMediator(50, apiService, database)
    ) {
        database.getRepoDao().getRepoList()
    }.flow
}