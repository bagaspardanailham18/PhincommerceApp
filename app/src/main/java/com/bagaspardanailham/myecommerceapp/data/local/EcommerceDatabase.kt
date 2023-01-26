package com.bagaspardanailham.myecommerceapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity

@Database(entities = [TrolleyEntity::class], version = 1)
abstract class EcommerceDatabase: RoomDatabase() {

    abstract fun ecommerceDao(): EcommerceDao

    companion object {
        @Volatile
        private var INSTANCE: EcommerceDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): EcommerceDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcommerceDatabase::class.java, "ecommerce_database"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}