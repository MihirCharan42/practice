package com.example.practice.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practice.model.data.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductDb: RoomDatabase() {

    abstract fun productDao(): ProductDao
}