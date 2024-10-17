package com.benwu.androidbase.repository

import com.benwu.androidbase.api.ApiService
import com.benwu.baselib.extension.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRepoList(perPage: Int = 50) = safeApiCall {
        apiService.getRepoList(1, perPage)
    }
}