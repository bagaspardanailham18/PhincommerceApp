package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.DataStock
import com.bagaspardanailham.myecommerceapp.data.DataStockItem
import com.bagaspardanailham.myecommerceapp.data.repository.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrollyViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    fun getAllProductFromTrolly() = repository.getAllProductFromTrolly()

    fun getAllCheckedProductFromTrolly() = repository.getAllCheckedProductFromTrolly()

    suspend fun updateProductQuantity(id: Int?, itemTotalPrice: Int?, quantity: Int?) = repository.updateProductData(id, itemTotalPrice, quantity)

    suspend fun updateProductIsCheckedAll(isChecked: Boolean) = repository.updateProductIsCheckedAll(isChecked)

    suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean) = repository.updateProductIsCheckedById(id, isChecked)

    fun deleteProductFromTrolly(context: Context, data: TrolleyEntity) = repository.removeProductFromTrolly(context, data)

    suspend fun deleteProductByIdFromTrolly(context: Context, id: Int?) = repository.removeProductByIdFromTrolly(context, id)

    suspend fun updateStock(accessToken: String, data: List<DataStockItem>, idUser: String) =
        repository.updateStock(accessToken, DataStock(idUser, data))
}