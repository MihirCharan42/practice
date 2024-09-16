package com.example.practice.model

import com.example.practice.model.data.Products
import com.example.practice.utils.ApiResult
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ProductsApi {

    @GET("/products")
    suspend fun getProducts(@Query("limit") limit: Int, @Query("skip") skip: Int): Response<Products>

    @GET("/products/search")
    suspend fun searchProducts(@Query("q") query: String?, @Query("limit") limit: Int = 30, @Query("skip") skip: Int): Response<Products>
}