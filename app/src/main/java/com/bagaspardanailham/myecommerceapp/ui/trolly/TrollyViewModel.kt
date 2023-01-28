package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrollyViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    fun getAllProductFromTrolly() = repository.getAllProductFromTrolly()

    suspend fun updateProductQuantity(id: Int?, itemTotalPrice: Int?, quantity: Int?) = repository.updateProductData(id, itemTotalPrice, quantity)

    suspend fun updateProductIsCheckedAll(isChecked: Boolean) = repository.updateProductIsCheckedAll(isChecked)

    suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean) = repository.updateProductIsCheckedById(id, isChecked)

    fun deleteProductFromTrolly(context: Context, data: TrolleyEntity) = repository.removeProductFromTrolly(context, data)

}