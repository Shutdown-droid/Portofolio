package com.example.e_commercevr.data.remote

import com.example.e_commercevr.BuildConfig
import com.example.e_commercevr.data.local.ProductEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AuraApiService {
    @GET("api/v1/products")
    suspend fun getProducts(): Response<List<ProductEntity>>

    companion object {
        fun create(): AuraApiService {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuraApiService::class.java)
        }
    }
}
