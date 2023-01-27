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

    fun deleteProductFromTrolly(context: Context, data: TrolleyEntity) = repository.removeProductFromTrolly(context, data)

}