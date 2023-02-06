package com.bagaspardanailham.myecommerceapp.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListPagingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductList(token: String, query: String?) = repository.getProductList(token, query)

    fun getProductListPaging(query: String?) = repository.getProductListPaging(query).cachedIn(viewModelScope)


}