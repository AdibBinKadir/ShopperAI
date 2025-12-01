# Quick Migration Guide: Adding SQLite to Other Features

This guide shows how to add SQLite database storage to other features in ShopperAI, using the chat history implementation as a template.

## Step-by-Step Process

### 1. Define Your Entity (Data Model)

Create a data class annotated with `@Entity`:

```kotlin
@Entity(tableName = "your_table_name")
data class YourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val field1: String,
    val field2: Int,
    val timestamp: Long = System.currentTimeMillis()
)
```

### 2. Create a DAO (Data Access Object)

Define database operations:

```kotlin
@Dao
interface YourDao {
    @Query("SELECT * FROM your_table_name ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<YourEntity>>
    
    @Insert
    suspend fun insert(item: YourEntity)
    
    @Update
    suspend fun update(item: YourEntity)
    
    @Delete
    suspend fun delete(item: YourEntity)
    
    @Query("DELETE FROM your_table_name")
    suspend fun deleteAll()
}
```

### 3. Update Database Class

Add your entity and DAO to the existing database:

```kotlin
@Database(
    entities = [
        ChatMessageEntity::class,
        YourEntity::class  // Add your entity here
    ], 
    version = 2,  // Increment version!
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun yourDao(): YourDao  // Add your DAO
    
    companion object {
        // ... existing code
    }
}
```

**Important**: Increment the version number and add a migration if needed, or use `.fallbackToDestructiveMigration()` for development.

### 4. Create Repository

Wrap your DAO in a repository:

```kotlin
class YourRepository(private val dao: YourDao) {
    
    val allItems: Flow<List<YourEntity>> = dao.getAllItems()
    
    suspend fun insert(item: YourEntity) {
        dao.insert(item)
    }
    
    suspend fun update(item: YourEntity) {
        dao.update(item)
    }
    
    suspend fun delete(item: YourEntity) {
        dao.delete(item)
    }
    
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
```

### 5. Create ViewModel

Manage your data with a ViewModel:

```kotlin
class YourViewModel(
    private val repository: YourRepository
) : ViewModel() {
    
    private val _items = MutableStateFlow<List<YourEntity>>(emptyList())
    val items: StateFlow<List<YourEntity>> = _items.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadItems()
    }
    
    private fun loadItems() {
        viewModelScope.launch {
            repository.allItems.collect { itemList ->
                _items.value = itemList
            }
        }
    }
    
    fun addItem(item: YourEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insert(item)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteItem(item: YourEntity) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
```

### 6. Create ViewModelFactory

```kotlin
class YourViewModelFactory(
    private val repository: YourRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### 7. Use in Composable

```kotlin
@Composable
fun YourScreen() {
    val context = LocalContext.current
    
    // Initialize
    val database = remember { ChatDatabase.getDatabase(context) }
    val repository = remember { YourRepository(database.yourDao()) }
    val viewModel: YourViewModel = viewModel(
        factory = YourViewModelFactory(repository)
    )
    
    // Collect state
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Your UI code
    LazyColumn {
        items(items) { item ->
            // Display item
        }
    }
}
```

## Common Use Cases

### Example 1: Search History

```kotlin
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val resultCount: Int = 0
)

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>
    
    @Insert
    suspend fun insertSearch(search: SearchHistoryEntity)
    
    @Query("DELETE FROM search_history WHERE id = :searchId")
    suspend fun deleteSearch(searchId: Long)
}
```

### Example 2: Favorite Products

```kotlin
@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val savedAt: Long = System.currentTimeMillis()
)

@Dao
interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE productId = :id)")
    fun isFavorite(id: String): Flow<Boolean>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(product: FavoriteProductEntity)
    
    @Query("DELETE FROM favorite_products WHERE productId = :id")
    suspend fun removeFavorite(id: String)
}
```

### Example 3: User Preferences

```kotlin
@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface UserPreferenceDao {
    @Query("SELECT value FROM user_preferences WHERE key = :key")
    fun getPreference(key: String): Flow<String?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(preference: UserPreferenceEntity)
}
```

## Database Migrations

When you change your schema (add/remove columns, tables), you need migrations:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Add a new column
        database.execSQL("ALTER TABLE chat_messages ADD COLUMN isRead INTEGER NOT NULL DEFAULT 0")
    }
}

fun getDatabase(context: Context): ChatDatabase {
    return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
            context.applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        )
        .addMigrations(MIGRATION_1_2)  // Add your migration
        .build()
        INSTANCE = instance
        instance
    }
}
```

## Best Practices

### 1. Always Use Coroutines
```kotlin
// ✅ Good
viewModelScope.launch {
    repository.insert(item)
}

// ❌ Bad - blocks UI thread
repository.insert(item)  // Would crash
```

### 2. Use Flow for Reactive Data
```kotlin
// ✅ Good - automatically updates UI
@Query("SELECT * FROM items")
fun getAllItems(): Flow<List<Item>>

// ❌ Less ideal - requires manual refresh
@Query("SELECT * FROM items")
suspend fun getAllItems(): List<Item>
```

### 3. Handle Errors Gracefully
```kotlin
viewModelScope.launch {
    try {
        repository.insert(item)
        _status.value = "Success"
    } catch (e: Exception) {
        _status.value = "Error: ${e.message}"
        Log.e("YourViewModel", "Database error", e)
    }
}
```

### 4. Use Indices for Performance
```kotlin
@Entity(
    tableName = "products",
    indices = [Index(value = ["productId"], unique = true)]
)
data class ProductEntity(...)
```

### 5. Don't Store Large Data
- Avoid storing images directly in SQLite
- Store URLs or file paths instead
- Keep database size reasonable

## Debugging Tips

### View Database in Android Studio
1. Open "App Inspection" tab (View > Tool Windows > App Inspection)
2. Select "Database Inspector"
3. View tables, run queries, modify data

### Common Queries for Debugging
```kotlin
// Check table contents
@Query("SELECT * FROM your_table")
suspend fun debugGetAll(): List<YourEntity>

// Count rows
@Query("SELECT COUNT(*) FROM your_table")
suspend fun getRowCount(): Int

// Clear all data (for testing)
@Query("DELETE FROM your_table")
suspend fun clearAll()
```

## Performance Tips

1. **Use indices** on frequently queried columns
2. **Limit results** with LIMIT clause
3. **Use pagination** for large datasets (Paging 3 library)
4. **Batch operations** when inserting multiple items
5. **Avoid complex queries** on UI thread (already handled by Room)

## Security Considerations

### Sensitive Data
```kotlin
// Consider SQLCipher for encryption
dependencies {
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
}
```

### Data Backup
```kotlin
// In AndroidManifest.xml
<application
    android:allowBackup="true"
    android:fullBackupContent="@xml/backup_rules"
    ...>
```

## Checklist for Adding SQLite

- [ ] Create Entity class with `@Entity`
- [ ] Create DAO interface with `@Dao`
- [ ] Add entity to Database class
- [ ] Increment database version
- [ ] Create Repository class
- [ ] Create ViewModel
- [ ] Create ViewModelFactory
- [ ] Update UI to use ViewModel
- [ ] Test insert/read/update/delete
- [ ] Test persistence across app restarts
- [ ] Add error handling
- [ ] Consider migrations for future changes

## Additional Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Flow Guide](https://developer.android.com/kotlin/flow)
- [ViewModel Best Practices](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Database Inspector](https://developer.android.com/studio/inspect/database)

## Summary

This pattern can be applied to any feature that needs data persistence:
1. Define your data model (Entity)
2. Create database operations (DAO)
3. Wrap in Repository
4. Manage with ViewModel
5. Display in UI

The chat history implementation serves as a complete, working example you can reference and adapt for your specific needs.
