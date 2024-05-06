package com.benwu.baselib.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.benwu.baselib.extension.isNullOrEmpty
import com.benwu.baselib.extension.print
import retrofit2.HttpException

@ExperimentalPagingApi
abstract class BaseRemoteMediator<T : Any>(private val loadSize: Int) : RemoteMediator<Int, T>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    1
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }

                LoadType.APPEND -> {
                    if (isNullOrEmpty(state.lastItemOrNull())) return MediatorResult.Success(true)
                    getNextKey(loadType, state)
                }
            }

            val dataList = getDataList(loadType, page, loadSize)

            MediatorResult.Success(isNullOrEmpty(dataList))
        } catch (e: Exception) {
            e.print()
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            e.print()
            MediatorResult.Error(e)
        }
    }

    abstract suspend fun getNextKey(loadType: LoadType, state: PagingState<Int, T>): Int

    abstract suspend fun getDataList(loadType: LoadType, page: Int, loadSize: Int): List<T>?
}