package com.example.practice.di

import android.content.Context
import androidx.room.Room
import com.example.practice.model.ProductsApi
import com.example.practice.model.local.ProductDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl("https://dummyjson.com")
        .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideProductsApi(retrofitBuilder: Retrofit.Builder) = retrofitBuilder.build().create(ProductsApi::class.java)

    @Singleton
    @Provides
    fun provideProductDb(@ApplicationContext applicationContext: Context): ProductDb = Room
        .databaseBuilder(applicationContext, ProductDb::class.java, "favourites_db")
        .build()
}