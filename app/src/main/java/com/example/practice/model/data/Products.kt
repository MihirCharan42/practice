package com.example.practice.model.data

data class Products(
    val limit: Int? = null,
    val products: ArrayList<Product>? = null,
    val skip: Int? = null,
    val total: Int? = null
)