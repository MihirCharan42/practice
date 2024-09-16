package com.example.practice.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.practice.model.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM favourites")
    suspend fun getAll(): ArrayList<Product>

    @Insert
    suspend fun addFavourite(product: Product)

    @Delete
    suspend fun deleteFavourite(product: Product)
}