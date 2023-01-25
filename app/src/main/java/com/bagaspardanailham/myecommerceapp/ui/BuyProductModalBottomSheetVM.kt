package com.bagaspardanailham.myecommerceapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import dagger.hilt.android.lifecycle.HiltViewModel

class BuyProductModalBottomSheetVM : ViewModel() {

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _price = MutableLiveData<Int>()
    val price: LiveData<Int> = _price

    init {
        _quantity.value = 1
    }

    fun increaseQuantity(stock: Int?) {
        if (_quantity.value!! < stock!!) {
            _quantity.value = _quantity.value?.plus(1)
        }
    }

    fun decreaseQuantity() {
        if (quantity.value == 1) {
            _quantity.value = 1
        } else {
            _quantity.value = _quantity.value?.minus(1)
        }
    }

}