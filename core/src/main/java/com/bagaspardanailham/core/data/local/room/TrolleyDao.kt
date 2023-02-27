package com.bagaspardanailham.core.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bagaspardanailham.core.data.local.model.TrolleyEntity

@Dao
interface TrolleyDao {

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

    @Query("SELECT * FROM trolley WHERE is_checked = '1'")
    fun getAllCheckedProduct(): LiveData<List<TrolleyEntity>>

    @Query("SELECT * FROM trolley WHERE id = :id")
    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>>

    @Query("SELECT count(*) from trolley where name_product = :name and id = :id")
    fun countDataById(id: Int?, name: String?): Int

    @Delete
    suspend fun deleteProductFromTrolly(trolly: TrolleyEntity)

    @Query("DELETE FROM trolley WHERE id = :id")
    suspend fun deleteProductByIdFromTrolly(id: Int?)

}