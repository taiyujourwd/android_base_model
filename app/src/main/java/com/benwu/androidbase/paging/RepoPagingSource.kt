package com.benwu.androidbase.paging

import com.benwu.androidbase.api.ApiService
import com.benwu.androidbase.data.Repo
import com.benwu.baselib.paging.BasePagingSource

class RepoPagingSource(private val apiService: ApiService) : BasePagingSource<Repo.Item>() {

    override suspend fun getDataList(page: Int, loadSize: Int) =
        apiService.getRepoList(page, loadSize).items
}