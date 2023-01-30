package com.bagaspardanailham.myecommerceapp.data

import android.os.Parcelable

data class DataStock(
    val data_stock: List<DataStockItem>
)

data class DataStockItem(
    val id_product: String,
    val stock: Int?
)
