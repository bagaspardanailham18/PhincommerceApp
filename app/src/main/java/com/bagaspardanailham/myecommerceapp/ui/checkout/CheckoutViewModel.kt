package com.bagaspardanailham.myecommerceapp.ui.checkout

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(val repository: EcommerceRepository): ViewModel() {

    suspend fun updateRate(idProduct: Int?, rate: String) = repository.updateRate(idProduct, rate)

}