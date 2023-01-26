package com.bagaspardanailham.myecommerceapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity

@Dao
interface EcommerceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductToTrolley(trolly: TrolleyEntity)

    @Query("UPDATE trolley SET quantity = :q WHERE id = :id")
    suspend fun updateProductQuantity(q: Int?, id: Int?)

    @Query("SELECT * FROM trolley")
    fun getAllProduct(): LiveData<List<TrolleyEntity>>

    @Query("SELECT * FROM trolley WHERE id = :id")
    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>>

    @Delete
    suspend fun deleteProductFromTrolly(trolly: TrolleyEntity)

}