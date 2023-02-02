package com.bagaspardanailham.myecommerceapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListPagingResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListPagingItem

class ProductPagingSource(private val search: String?, private val apiService: ApiService) : PagingSource<Int, ProductListPagingItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductListPagingItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getProductListPaging(search, position)

            LoadResult.Page(
                data = responseData.success!!.data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.success.data.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductListPagingItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}