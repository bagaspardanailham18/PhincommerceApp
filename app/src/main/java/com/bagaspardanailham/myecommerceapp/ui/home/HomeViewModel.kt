package com.bagaspardanailham.myecommerceapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListPagingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductList(token: String, query: String?) = repository.getProductList(token, query)

    suspend fun productListPaging(search: String?): LiveData<PagingData<ProductListPagingItem>> =
        repository.getProductListPaging(search).cachedIn(viewModelScope)

}