package com.bagaspardanailham.myecommerceapp.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getProductDetail(accessToken: String, idProduct: Int?, idUser: Int?) = repository.getProductDetail(accessToken, idProduct, idUser)

    suspend fun addProductToFavorite(accessToken: String, idProduct: Int?, idUser: Int?) = repository.addProductToFavorite(accessToken, idProduct, idUser)

    suspend fun removeProductFromFavorite(accessToken: String, idProduct: Int?, idUser: Int?) = repository.removeProductFromFavorite(accessToken, idProduct, idUser)

    suspend fun addProductToTrolley(context: Context, dataProduct: TrolleyEntity) = repository.addProductToTrolly(context, dataProduct)

    fun getProductById(idProduct: Int?) = repository.getProductById(idProduct)

    fun countDataById(id: Int?, name: String?): Int = repository.countDataById(id, name)

    suspend fun getOtherProducts(idUser: Int?) = repository.getOtherProductList(idUser)

    suspend fun getProductSearchHistory(idUser: Int?) = repository.getProductSearchHistory(idUser)
}