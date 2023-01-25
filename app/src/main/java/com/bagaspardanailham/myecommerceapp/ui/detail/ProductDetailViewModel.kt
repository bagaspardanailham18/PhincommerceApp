package com.bagaspardanailham.myecommerceapp.ui.detail

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductDetail(accessToken: String, idProduct: Int?, idUser: Int?) = repository.getProductDetail(accessToken, idProduct, idUser)

    suspend fun addProductToFavorite(accessToken: String, idProduct: Int?, idUser: Int?) = repository.addProductToFavorite(accessToken, idProduct, idUser)

    suspend fun removeProductFromFavorite(accessToken: String, idProduct: Int?, idUser: Int?) = repository.removeProductFromFavorite(accessToken, idProduct, idUser)

}