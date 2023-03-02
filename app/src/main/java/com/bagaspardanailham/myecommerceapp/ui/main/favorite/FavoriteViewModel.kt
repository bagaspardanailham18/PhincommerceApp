package com.bagaspardanailham.myecommerceapp.ui.main.favorite

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    suspend fun getFavoriteProductList(query: String?, id: Int) = repository.getFavoriteProductList(query, id)

}