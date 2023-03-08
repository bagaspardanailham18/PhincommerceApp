package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.DataStock
import com.bagaspardanailham.core.data.DataStockItem
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrollyViewModel @Inject constructor(private val localRepository: LocalRepository, private val ecommerceRepository: EcommerceRepository): ViewModel() {

    fun getAllProductFromTrolly() = localRepository.getAllProductFromTrolly()

    fun getAllCheckedProductFromTrolly() = localRepository.getAllCheckedProductFromTrolly()

    suspend fun updateProductQuantity(id: Int?, itemTotalPrice: Int?, quantity: Int?) = localRepository.updateProductData(id, itemTotalPrice, quantity)

    suspend fun updateProductIsCheckedAll(isChecked: Boolean) = localRepository.updateProductIsCheckedAll(isChecked)

    suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean) = localRepository.updateProductIsCheckedById(id, isChecked)

    fun deleteProductFromTrolly(context: Context, data: TrolleyEntity) = localRepository.removeProductFromTrolly(context, data)

    suspend fun deleteProductByIdFromTrolly(context: Context, id: Int?) = localRepository.removeProductByIdFromTrolly(context, id)

    suspend fun updateStock(data: List<DataStockItem>, idUser: String) =
        ecommerceRepository.updateStock(DataStock(idUser, data))
}