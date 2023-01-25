package com.bagaspardanailham.myecommerceapp.ui.detail

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductDetail(accessToken: String, idProduct: Int) = repository.getProductDetail(accessToken, idProduct)

}