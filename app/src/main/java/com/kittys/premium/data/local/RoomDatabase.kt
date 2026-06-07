package com.kittys.premium.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════
//   ROOM ENTITIES  — local cache / offline support
// ═══════════════════════════════════════════════════════

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id              : String,
    val name             : String,
    val description      : String,
    val brand            : String,
    val vendor_id        : String,
    val vendor_name      : String,
    val vendor_rating    : Float,
    val price_lkr        : Int,
    val original_price   : Int?,
    val discount_percent : Int,
    val images           : String,     // JSON array stored as String
    val sizes            : String,     // JSON array stored as String
    val colors           : String,     // JSON array stored as String
    val category         : String,
    val tags             : String,     // JSON array stored as String
    val rating           : Float,
    val review_count     : Int,
    val is_wishlisted    : Boolean = false,
    val stock_qty        : Int,
    val created_at       : Long    = System.currentTimeMillis(),
    val cached_at        : Long    = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id         : String,
    val product_id : String,
    val name       : String,
    val image_url  : String,
    val brand      : String,
    val size       : String,
    val color      : String,
    val price_lkr  : Int,
    val quantity   : Int
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val product_id : String,
    val added_at   : Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query      : String,
    val searched_at : Long = System.currentTimeMillis()
)

@Entity(tableName = "child_profiles")
data class ChildProfileEntity(
    @PrimaryKey val id          : String,
    val user_id     : String,
    val name        : String,
    val gender      : String,
    val date_of_birth: String,
    val age_in_years : Int,
    val height_cm   : Int?,
    val weight_kg   : Float?,
    val current_size: String
)


// ═══════════════════════════════════════════════════════
//   TYPE CONVERTERS  — for JSON arrays ↔ String
// ═══════════════════════════════════════════════════════

class Converters {
    private val gson = com.google.gson.Gson()

    @TypeConverter
    fun listToJson(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun jsonToList(json: String): List<String> =
        gson.fromJson(json, Array<String>::class.java).toList()
}


// ═══════════════════════════════════════════════════════
//   DAOs
// ═══════════════════════════════════════════════════════

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE category = :category ORDER BY rating DESC LIMIT 20")
    fun getByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY created_at DESC LIMIT 10")
    fun getNewArrivals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("UPDATE products SET is_wishlisted = :wishlisted WHERE id = :id")
    suspend fun updateWishlist(id: String, wishlisted: Boolean)

    // Delete cached products older than 1 hour
    @Query("DELETE FROM products WHERE cached_at < :cutoff")
    suspend fun deleteStale(cutoff: Long = System.currentTimeMillis() - 3_600_000)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items ORDER BY rowid ASC")
    fun getAll(): Flow<List<CartItemEntity>>

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :qty WHERE id = :id")
    suspend fun updateQty(id: String, qty: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT SUM(price_lkr * quantity) FROM cart_items")
    fun getTotalPrice(): Flow<Int?>

    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getById(id: String): CartItemEntity?
}

@Dao
interface WishlistDao {

    @Query("SELECT product_id FROM wishlist")
    fun getAll(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE product_id = :productId")
    suspend fun delete(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE product_id = :productId)")
    suspend fun isWishlisted(productId: String): Boolean
}

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_searches ORDER BY searched_at DESC LIMIT 5")
    fun getAll(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}

@Dao
interface ChildProfileDao {

    @Query("SELECT * FROM child_profiles WHERE user_id = :userId")
    fun getByUser(userId: String): Flow<List<ChildProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(child: ChildProfileEntity)

    @Query("DELETE FROM child_profiles WHERE id = :id")
    suspend fun delete(id: String)
}


// ═══════════════════════════════════════════════════════
//   ROOM DATABASE
// ═══════════════════════════════════════════════════════

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        RecentSearchEntity::class,
        ChildProfileEntity::class
    ],
    version  = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MikoDatabase : RoomDatabase() {

    abstract fun productDao()      : ProductDao
    abstract fun cartDao()         : CartDao
    abstract fun wishlistDao()     : WishlistDao
    abstract fun recentSearchDao() : RecentSearchDao
    abstract fun childProfileDao() : ChildProfileDao

    companion object {
        const val DATABASE_NAME = "miko_premium.db"
    }
}


// ═══════════════════════════════════════════════════════
//   DI MODULE UPDATE — add Room to Hilt
// ═══════════════════════════════════════════════════════

// Add this to your NetworkModule or create a new DatabaseModule:

/*
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KittysDatabase =
        Room.databaseBuilder(
            context,
            KittysDatabase::class.java,
            KittysDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides fun provideProductDao(db: KittysDatabase)      = db.productDao()
    @Provides fun provideCartDao(db: KittysDatabase)         = db.cartDao()
    @Provides fun provideWishlistDao(db: KittysDatabase)     = db.wishlistDao()
    @Provides fun provideRecentSearchDao(db: KittysDatabase) = db.recentSearchDao()
    @Provides fun provideChildProfileDao(db: KittysDatabase) = db.childProfileDao()
}
*/
