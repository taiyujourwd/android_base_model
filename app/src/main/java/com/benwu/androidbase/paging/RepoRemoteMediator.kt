package com.benwu.androidbase.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.benwu.androidbase.api.ApiService
import com.benwu.androidbase.data.RemoteKey
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.db.DemoDatabase
import com.benwu.baselib.extension.isNullOrEmpty
import com.benwu.baselib.paging.BaseRemoteMediator

@ExperimentalPagingApi
class RepoRemoteMediator(
    private val loadSize: Int,
    private val apiService: ApiService,
    private val database: DemoDatabase
) : BaseRemoteMediator<Repo.Item>(loadSize) {
    private val remoteKeyDao = database.getRemoteKeyDao()
    private val repoDao = database.getRepoDao()

    override suspend fun getNextKey(loadType: LoadType, state: PagingState<Int, Repo.Item>) =
        remoteKeyDao.getRemoteKey("repo").nextKey + 1

    override suspend fun getDataList(
        loadType: LoadType,
        page: Int,
        loadSize: Int
    ): List<Repo.Item>? {
        val repoList = apiService.getRepoList(page, loadSize).items

        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.delete("repo")
                repoDao.deleteAll()
            }

            if (!isNullOrEmpty(repoList)) {
                remoteKeyDao.insert(RemoteKey("repo", page))
                repoDao.insert(*repoList!!.toTypedArray())
            }
        }

        return repoList
    }
}