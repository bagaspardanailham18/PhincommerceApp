package com.bagaspardanailham.myecommerceapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bagaspardanailham.myecommerceapp.data.DataStock
import com.bagaspardanailham.myecommerceapp.data.DataStockItem
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity

@Dao
interface EcommerceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductToTrolley(trolly: TrolleyEntity)

    @Query("UPDATE trolley SET quantity = :q, item_total_price = :itemTotalPrice WHERE id = :id")
    suspend fun updateProductData(q: Int?, itemTotalPrice: Int?, id: Int?)

    @Query("UPDATE trolley SET is_checked = :isChecked")
    suspend fun updateProductIsCheckedAll(isChecked: Boolean)

    @Query("UPDATE trolley SET is_checked = :isChecked WHERE id = :id")
    suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?)

    @Query("SELECT * FROM trolley")
    fun getAllProduct(): LiveData<List<TrolleyEntity>>

//    @Query("SELECT id, stock FROM trolley WHERE is_checked = '1'")
//    fun getAllCheckedProduct(): LiveData<List<DataStockItem>>

    @Query("SELECT * FROM trolley WHERE id = :id")
    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>>

    @Delete
    suspend fun deleteProductFromTrolly(trolly: TrolleyEntity)

}