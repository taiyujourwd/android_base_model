package com.benwu.androidbase.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.benwu.androidbase.data.Repo

interface ApiService {

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun getRepoList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Repo

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}