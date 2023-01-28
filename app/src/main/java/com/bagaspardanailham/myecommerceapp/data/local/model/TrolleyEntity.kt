package com.bagaspardanailham.myecommerceapp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bagaspardanailham.myecommerceapp.data.remote.response.ImageProductItem
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trolley")
data class TrolleyEntity(
    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "name_product")
    val nameProduct: String?,

    @ColumnInfo(name = "quantity")
    val quantity: Int? = 1,

    @ColumnInfo(name = "price")
    val price: String?,

    @ColumnInfo(name = "item_total_price")
    val itemTotalPrice: Int?,

    @ColumnInfo(name = "stock")
    val stock: Int? = null,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int? = null
)
