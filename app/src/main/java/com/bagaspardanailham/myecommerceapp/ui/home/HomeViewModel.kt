package com.bagaspardanailham.myecommerceapp.ui.home

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductList(token: String, query: String?) = repository.getProductList(token, query)

}