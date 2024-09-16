package com.example.practice.model

import com.example.practice.model.data.Product
import com.example.practice.model.data.Products
import com.example.practice.model.local.ProductDb
import retrofit2.Response
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val productsApi: ProductsApi, private val productDb: ProductDb) {

    suspend fun getFavourites(): ArrayList<Product> {
        return productDb.productDao().getAll()
    }

    suspend fun addFavourites(product: Product) {
        productDb.productDao().addFavourite(product)
    }

    suspend fun deleteFavourites(product: Product) {
        productDb.productDao().deleteFavourite(product)
    }

    suspend fun getProducts(limit: Int, skip: Int): Response<Products> {
        return productsApi.getProducts(limit, skip)
    }

    suspend fun searchProducts(query: String?, limit: Int, skip: Int): Response<Products> {
        return productsApi.searchProducts(query, limit, skip)
    }
}