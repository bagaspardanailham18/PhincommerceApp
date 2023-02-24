package com.bagaspardanailham.myecommerceapp.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bagaspardanailham.myecommerceapp.data.repository.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseAnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: EcommerceRepository, private val firebaseAnalyticsRepository: FirebaseAnalyticsRepository): ViewModel() {

    suspend fun getProductList(token: String, query: String?) = repository.getProductList(token, query)

    fun getProductListPaging(query: String?) = repository.getProductListPaging(query).cachedIn(viewModelScope)


    // Analytics
    fun onLoadScreen(screenName: String, screenClass: String) {
        firebaseAnalyticsRepository.onLoadScreen(screenName, screenClass)
    }
    fun onPagingScroll(offset: Int) {
        firebaseAnalyticsRepository.onPagingScroll(offset)
    }
    fun onSearch(query: String?) {
        firebaseAnalyticsRepository.onSearch(query)
    }
    fun onClickProduct(productId: Int, productName: String, rate: Int, price: Double) {
        firebaseAnalyticsRepository.onClickProduct(productId, productName, price, rate)
    }
    fun onClickTrolleyIcon() {
        firebaseAnalyticsRepository.onClickTrolleyIcon()
    }
    fun onClickNotificationIcon() {
        firebaseAnalyticsRepository.onClickNotificationIcon()
    }

}