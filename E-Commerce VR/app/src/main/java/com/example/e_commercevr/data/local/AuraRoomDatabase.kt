package com.example.e_commercevr.data.local

import androidx.room.*
import com.example.e_commercevr.ui.components.ProductItem
import kotlinx.coroutines.flow.Flow

// Kita gunakan ProductItem langsung agar tidak perlu konversi Entity ke UI Model
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val price: Long,
    val originalPrice: Long?,
    val imageUrl: String,
    val isArSupported: Boolean,
    val model3dUrl: String
)

// Fungsi helper untuk mengubah Entity menjadi ProductItem (UI Model)
fun ProductEntity.toProductItem() = ProductItem(
    id = id, title = title, category = category, price = price,
    originalPrice = originalPrice, imageUrl = imageUrl,
    isArSupported = isArSupported, model3dUrl = model3dUrl
)

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
}

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AuraRoomDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AuraRoomDatabase? = null

        fun getDatabase(context: android.content.Context): AuraRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AuraRoomDatabase::class.java,
                    "aura_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}