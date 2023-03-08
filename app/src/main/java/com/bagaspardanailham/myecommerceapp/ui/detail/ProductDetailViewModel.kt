package com.bagaspardanailham.myecommerceapp.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(private val repository: EcommerceRepository, private val localRepository: LocalRepository): ViewModel() {

    suspend fun getProductDetail(idProduct: Int?, idUser: Int?) = repository.getProductDetail(idProduct, idUser)

    suspend fun addProductToFavorite(idProduct: Int?, idUser: Int?) = repository.addProductToFavorite(idProduct, idUser)

    suspend fun removeProductFromFavorite(idProduct: Int?, idUser: Int?) = repository.removeProductFromFavorite(idProduct, idUser)

    suspend fun addProductToTrolley(context: Context, dataProduct: TrolleyEntity) = localRepository.addProductToTrolly(context, dataProduct)

    fun getProductById(idProduct: Int?) = localRepository.getProductById(idProduct)

    fun countDataById(id: Int?, name: String?): Int = localRepository.countDataById(id, name)

    suspend fun getOtherProducts(idUser: Int?) = repository.getOtherProductList(idUser)

    suspend fun getProductSearchHistory(idUser: Int?) = repository.getProductSearchHistory(idUser)
}