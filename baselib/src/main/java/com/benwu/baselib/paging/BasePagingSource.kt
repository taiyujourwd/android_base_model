package com.benwu.baselib.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.benwu.baselib.extension.debugPrint
import com.benwu.baselib.extension.isNullOrEmpty
import retrofit2.HttpException

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>) = try {
        val page = params.key ?: 1
        val dataList = getDataList(page, params.loadSize)

        LoadResult.Page(
            dataList ?: listOf(),
            getPrevKey(page, dataList),
            getNextKey(page, dataList)
        )
    } catch (e: Exception) {
        e.debugPrint()
        LoadResult.Error(e)
    } catch (e: HttpException) {
        e.debugPrint()
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, T>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    abstract suspend fun getDataList(page: Int, loadSize: Int): List<T>?

    open fun getPrevKey(page: Int, dataList: List<T>?): Int? = null

    open fun getNextKey(page: Int, dataList: List<T>?): Int? =
        if (!isNullOrEmpty(dataList)) page + 1 else null
}