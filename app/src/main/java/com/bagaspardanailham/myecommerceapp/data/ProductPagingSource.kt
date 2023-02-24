package com.bagaspardanailham.myecommerceapp.data

import android.os.Bundle
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListPagingResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListPagingItem
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.utils.Constant
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME
import javax.inject.Inject

class ProductPagingSource(private val search: String?, private val apiService: ApiService) : PagingSource<Int, ProductListPagingItem>() {

//    @Inject
//    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductListPagingItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getProductListPaging(search, position)

            //firebaseAnalyticsRepository.onPagingScroll(position)

            LoadResult.Page(
                data = responseData.success!!.data,
                prevKey = null,
                nextKey = if (responseData.success.data.isEmpty()) null else position + 5,
                position
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