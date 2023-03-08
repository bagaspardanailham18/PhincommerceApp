package com.bagaspardanailham.myecommerceapp.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: EcommerceRepository, private val firebaseAnalyticsRepository: FirebaseAnalyticsRepository): ViewModel() {

    //suspend fun getProductList(token: String, query: String?) = repository.getProductList(token, query)

    suspend fun getProductListPaging(query: String?) = repository.getProductListPaging(query).cachedIn(viewModelScope)


}