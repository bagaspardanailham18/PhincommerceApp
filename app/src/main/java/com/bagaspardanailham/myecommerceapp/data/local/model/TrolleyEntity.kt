package com.bagaspardanailham.myecommerceapp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bagaspardanailham.myecommerceapp.data.remote.response.ImageProductItem
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trolley")
data class TrolleyEntity(
    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "name_product")
    val nameProduct: String? = null,

    @ColumnInfo(name = "harga")
    val harga: String? = null,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int? = null
)
