package com.example.practice.model.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favourites")
@Parcelize
data class Product(
    @PrimaryKey
    val id: Int? = null,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val images: List<String>? = null,
    val price: Double? = null,
    val rating: Double? = null,
    val title: String? = null,
) : Parcelable