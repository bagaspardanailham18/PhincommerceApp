package com.bagaspardanailham.myecommerceapp.ui.detail.bottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.DataStock
import com.bagaspardanailham.myecommerceapp.data.DataStockItem
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bumptech.glide.Glide.init
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BuyProductModalBottomSheetVM @Inject constructor(val repository: EcommerceRepository) : ViewModel() {

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _price = MutableLiveData<Int>()
    val price: LiveData<Int> = _price

    private var initPrice: Int? = 0

    init {
        _quantity.value = 1
    }

    fun increaseQuantity(stock: Int?) {
        if (_quantity.value!! < stock!!) {
            _quantity.value = _quantity.value?.plus(1)
            _price.value = initPrice?.times(_quantity.value!!.toInt())
        }
    }

    fun decreaseQuantity() {
        if (quantity.value == 1) {
            _quantity.value = 1
            _price.value = initPrice!!.toInt()
        } else {
            _quantity.value = _quantity.value?.minus(1)
            _price.value = initPrice?.times(_quantity.value!!.toInt())
        }
    }

    fun setPrice(productPrice: Int) {
        initPrice = productPrice
        _price.value = productPrice
    }

    suspend fun updateStock(accessToken: String, idProduct: String, stock: Int?, idUser: String) =
        repository.updateStock(accessToken, DataStock(idUser, listOf(DataStockItem(idProduct, stock))))

}