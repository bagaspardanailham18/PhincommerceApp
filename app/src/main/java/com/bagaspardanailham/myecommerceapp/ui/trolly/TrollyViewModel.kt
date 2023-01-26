package com.bagaspardanailham.myecommerceapp.ui.trolly

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrollyViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    fun getAllProductFromTrolly() = repository.getAllProductFromTrolly()

}