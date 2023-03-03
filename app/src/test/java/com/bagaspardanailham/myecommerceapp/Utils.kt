package com.bagaspardanailham.myecommerceapp

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.bagaspardanailham.core.data.remote.response.product.ProductListPagingItem

class ProductPagingSource : PagingSource<Int, LiveData<List<ProductListPagingItem>>>() {
    companion object {
        fun snapshot(items: List<ProductListPagingItem>): PagingData<ProductListPagingItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ProductListPagingItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ProductListPagingItem>>> {
        return PagingSource.LoadResult.Page(emptyList(), 0, 1)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}