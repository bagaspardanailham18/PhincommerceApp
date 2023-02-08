package com.bagaspardanailham.myecommerceapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DataStock(
    val id_user: String,
    val data_stock: List<DataStockItem>
)

@Parcelize
data class DataStockItem(
    val id_product: String,
    val stock: Int?
) : Parcelable
