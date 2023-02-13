package com.bagaspardanailham.myecommerceapp.ui.main.favorite

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getFavoriteProductList(token: String, query: String?, id: Int) = repository.getFavoriteProductList(token, query, id)

}