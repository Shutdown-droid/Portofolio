package com.example.e_commercevr.data.repository

import android.content.Context
import com.example.e_commercevr.data.local.AuraRoomDatabase
import com.example.e_commercevr.data.local.ProductDao
import com.example.e_commercevr.data.local.toProductItem
import com.example.e_commercevr.data.remote.AuraApiService
import com.example.e_commercevr.ui.components.ProductItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val productDao: ProductDao,
    private val apiService: AuraApiService = AuraApiService.create()
) {
    constructor(context: Context) : this(
        productDao = AuraRoomDatabase.getDatabase(context).productDao(),
        apiService = AuraApiService.create()
    )

    private val seedProduct = ProductItem(
        id = "sofa-001",
        title = "Sofa Scandinavian Minimalis",
        category = "Furnitur",
        price = 4250000L,
        originalPrice = 4950000L,
        imageUrl = "sofa.jpg",
        isArSupported = true,
        model3dUrl = "sofa.glb",
        rating = 4.8,
        totalReviews = 96,
        sold = 120,
        location = "Jakarta Selatan",
        variants = listOf("Krem", "Abu-abu", "Coklat Tua"),
        specs = listOf("Panjang" to "180 cm", "Tinggi" to "85 cm", "Kedalaman" to "90 cm")
    )

    val products: Flow<List<ProductItem>> = productDao.getAllProducts().map { entities ->
        if (entities.isEmpty()) {
            listOf(seedProduct)
        } else {
            entities.map { it.toProductItem() }
        }
    }

    suspend fun fetchAndSaveProducts() {
        try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                productDao.clearAllProducts()
                productDao.insertProducts(response.body()!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
