# Code Reference - SQLite Chat History

Quick reference for key code patterns and usage examples.

## 📝 Core Components

### 1. Entity Definition

```kotlin
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String = ""
)
```

**Key Points:**
- `@Entity` marks class as database table
- `@PrimaryKey` with `autoGenerate = true` for auto-increment
- All fields must be val (immutable)
- Use primitive types or Room-supported types

---

### 2. DAO Interface

```kotlin
@Dao
interface ChatMessageDao {
    // Returns Flow for reactive updates
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>
    
    // Suspend for async operations
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)
    
    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}
```

**Key Points:**
- `@Dao` marks interface for Room
- Use `Flow` for queries that observe changes
- Use `suspend` for one-time operations
- Room generates implementation automatically

---

### 3. Database Class

```kotlin
@Database(entities = [ChatMessageEntity::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    
    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null
        
        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Key Points:**
- Singleton pattern with `@Volatile` and `synchronized`
- Use `applicationContext` to avoid leaks
- `.fallbackToDestructiveMigration()` for development
- Thread-safe initialization

---

### 4. Repository Pattern

```kotlin
class ChatRepository(private val chatDao: ChatMessageDao) {
    
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    
    suspend fun insertMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }
    
    suspend fun deleteAllMessages() {
        chatDao.deleteAllMessages()
    }
}
```

**Key Points:**
- Wraps DAO for clean separation
- Exposes Flow directly from DAO
- Can add business logic here
- Makes testing easier

---

### 5. ViewModel with Database

```kotlin
class AiAssistantViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()
    
    init {
        loadMessages()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.allMessages.collect { messageList ->
                _messages.value = messageList
            }
        }
    }
    
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            val message = ChatMessageEntity(
                text = text,
                isUser = true
            )
            chatRepository.insertMessage(message)
        }
    }
}
```

**Key Points:**
- Use `viewModelScope` for automatic cleanup
- Collect Flow in init
- Update StateFlow for UI observation
- Always check input validity

---

### 6. ViewModelFactory

```kotlin
class AiAssistantViewModelFactory(
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AiAssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiAssistantViewModel(chatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

**Key Points:**
- Required for dependency injection
- Checks ViewModel type
- Passes repository to ViewModel
- Returns properly typed ViewModel

---

### 7. Composable Usage

```kotlin
@Composable
fun AiAssistantScreen() {
    val context = LocalContext.current
    
    // Initialize database and repository
    val database = remember { ChatDatabase.getDatabase(context) }
    val repository = remember { ChatRepository(database.chatMessageDao()) }
    
    // Create ViewModel with factory
    val viewModel: AiAssistantViewModel = viewModel(
        factory = AiAssistantViewModelFactory(repository)
    )
    
    // Collect state
    val messages by viewModel.messages.collectAsState()
    
    // UI
    LazyColumn {
        items(messages, key = { it.id }) { message ->
            MessageBubble(message)
        }
    }
}
```

**Key Points:**
- Use `remember` to persist across recompositions
- Pass context to database
- Use factory for ViewModel creation
- Collect StateFlow with `collectAsState()`

---

## 🔄 Common Patterns

### Pattern 1: Insert Data

```kotlin
// In ViewModel
fun addMessage(text: String) {
    viewModelScope.launch {
        val message = ChatMessageEntity(
            text = text,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        repository.insertMessage(message)
    }
}
```

### Pattern 2: Query Data

```kotlin
// In DAO
@Query("SELECT * FROM chat_messages WHERE isUser = :isUser")
fun getMessagesByType(isUser: Boolean): Flow<List<ChatMessageEntity>>

// In Repository
fun getUserMessages(): Flow<List<ChatMessageEntity>> {
    return chatDao.getMessagesByType(isUser = true)
}
```

### Pattern 3: Update Data

```kotlin
// In DAO
@Update
suspend fun updateMessage(message: ChatMessageEntity)

// In ViewModel
fun editMessage(messageId: Long, newText: String) {
    viewModelScope.launch {
        val message = _messages.value.find { it.id == messageId }
        message?.let {
            val updated = it.copy(text = newText)
            repository.updateMessage(updated)
        }
    }
}
```

### Pattern 4: Delete Data

```kotlin
// In DAO
@Delete
suspend fun deleteMessage(message: ChatMessageEntity)

@Query("DELETE FROM chat_messages WHERE id = :messageId")
suspend fun deleteById(messageId: Long)

// In ViewModel
fun deleteMessage(message: ChatMessageEntity) {
    viewModelScope.launch {
        repository.deleteMessage(message)
    }
}
```

### Pattern 5: Complex Queries

```kotlin
// Count messages
@Query("SELECT COUNT(*) FROM chat_messages")
suspend fun getMessageCount(): Int

// Get recent messages
@Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
fun getRecentMessages(limit: Int): Flow<List<ChatMessageEntity>>

// Search messages
@Query("SELECT * FROM chat_messages WHERE text LIKE '%' || :query || '%'")
fun searchMessages(query: String): Flow<List<ChatMessageEntity>>

// Date range
@Query("SELECT * FROM chat_messages WHERE timestamp BETWEEN :start AND :end")
fun getMessagesBetween(start: Long, end: Long): Flow<List<ChatMessageEntity>>
```

---

## 🎯 Best Practices

### ✅ DO

```kotlin
// ✅ Use suspend functions for one-time operations
@Insert
suspend fun insertMessage(message: ChatMessageEntity)

// ✅ Use Flow for observing data
@Query("SELECT * FROM chat_messages")
fun getAllMessages(): Flow<List<ChatMessageEntity>>

// ✅ Use viewModelScope
viewModelScope.launch {
    repository.insertMessage(message)
}

// ✅ Handle errors
viewModelScope.launch {
    try {
        repository.insertMessage(message)
    } catch (e: Exception) {
        _error.value = "Failed to save"
    }
}

// ✅ Use specific types
data class ChatMessageEntity(
    val text: String,  // Not Any or Object
    val isUser: Boolean  // Not Int
)
```

### ❌ DON'T

```kotlin
// ❌ Don't use suspend on Flow
@Query("SELECT * FROM chat_messages")
suspend fun getAllMessages(): Flow<List<ChatMessageEntity>>  // Wrong!

// ❌ Don't call suspend from main thread
fun sendMessage() {
    repository.insertMessage(message)  // Crashes!
}

// ❌ Don't forget to collect Flow
val messages = viewModel.messages  // Wrong! Must collectAsState()

// ❌ Don't create new coroutine scope
GlobalScope.launch {  // Wrong! Use viewModelScope
    repository.insertMessage(message)
}

// ❌ Don't make entity var
data class ChatMessageEntity(
    var text: String  // Wrong! Use val
)
```

---

## 🔍 Debugging Snippets

### Enable SQL Logging

```kotlin
Room.databaseBuilder(...)
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomSQL", "Query: $sqlQuery")
        Log.d("RoomArgs", "Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
```

### Log Flow Emissions

```kotlin
repository.allMessages
    .onEach { messages ->
        Log.d("ChatRepo", "Emitting ${messages.size} messages")
    }
    .collect { messageList ->
        _messages.value = messageList
    }
```

### Verify Database State

```kotlin
// Add to ViewModel for debugging
fun debugDatabase() {
    viewModelScope.launch {
        val count = chatDao.getMessageCount()
        Log.d("ChatDebug", "Total messages: $count")
        
        val messages = chatRepository.allMessages.first()
        messages.forEach { msg ->
            Log.d("ChatDebug", "Message: ${msg.id} - ${msg.text}")
        }
    }
}
```

---

## 📊 Performance Optimizations

### Add Indices

```kotlin
@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["sessionId"]),
        Index(value = ["isUser"])
    ]
)
data class ChatMessageEntity(...)
```

### Use Transactions

```kotlin
@Dao
interface ChatMessageDao {
    @Transaction
    suspend fun replaceAllMessages(messages: List<ChatMessageEntity>) {
        deleteAllMessages()
        messages.forEach { insertMessage(it) }
    }
}
```

### Batch Insert

```kotlin
@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertMessages(messages: List<ChatMessageEntity>)
}

// Usage
val messages = listOf(
    ChatMessageEntity("Message 1", true),
    ChatMessageEntity("Message 2", false)
)
chatDao.insertMessages(messages)
```

### Limit Results

```kotlin
@Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
fun getRecentMessages(limit: Int = 50): Flow<List<ChatMessageEntity>>
```

---

## 🧪 Testing Snippets

### Basic DAO Test

```kotlin
@Test
fun insertAndRetrieve() = runBlocking {
    val message = ChatMessageEntity("Test", true)
    chatDao.insertMessage(message)
    
    val result = chatDao.getAllMessages().first()
    assertEquals(1, result.size)
    assertEquals("Test", result[0].text)
}
```

### ViewModel Test

```kotlin
@Test
fun `sendMessage updates state`() = runBlocking {
    viewModel.sendMessage("Hello")
    
    advanceUntilIdle()
    
    val messages = viewModel.messages.value
    assertTrue(messages.any { it.text == "Hello" })
}
```

---

## 🔐 Security Snippets

### Encrypt Database

```kotlin
dependencies {
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
}

val passphrase = "your-secret-key".toByteArray()
val factory = SupportFactory(passphrase)

Room.databaseBuilder(...)
    .openHelperFactory(factory)
    .build()
```

### Validate Input

```kotlin
fun sendMessage(text: String) {
    // Sanitize input
    val sanitized = text.trim()
        .take(5000)  // Max length
        .replace(Regex("[<>]"), "")  // Remove dangerous chars
    
    if (sanitized.isBlank()) return
    
    viewModelScope.launch {
        repository.insertMessage(
            ChatMessageEntity(sanitized, true)
        )
    }
}
```

---

## 📱 UI Integration Patterns

### Loading State

```kotlin
@Composable
fun ChatScreen(viewModel: AiAssistantViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(messages) { message ->
                MessageBubble(message)
            }
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
```

### Error Handling

```kotlin
@Composable
fun ChatScreen(viewModel: AiAssistantViewModel) {
    val error by viewModel.error.collectAsState()
    
    error?.let { errorMessage ->
        Snackbar(
            action = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(errorMessage)
        }
    }
}
```

### Empty State

```kotlin
@Composable
fun MessageList(messages: List<ChatMessageEntity>) {
    if (messages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ChatBubble, null)
                Text("No messages yet")
                Text("Start a conversation!")
            }
        }
    } else {
        LazyColumn {
            items(messages) { message ->
                MessageBubble(message)
            }
        }
    }
}
```

---

## 🎨 Styling Patterns

### Message Bubble

```kotlin
@Composable
fun MessageBubble(message: ChatMessageEntity) {
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start
    val colors = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            colors = CardDefaults.cardColors(containerColor = colors)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
```

---

## 📚 Quick Reference

### Common Annotations
- `@Entity` - Database table
- `@PrimaryKey` - Primary key field
- `@ColumnInfo` - Custom column name
- `@Ignore` - Exclude from database
- `@Dao` - Data access object
- `@Database` - Database class
- `@Query` - SQL query
- `@Insert` - Insert operation
- `@Update` - Update operation
- `@Delete` - Delete operation
- `@Transaction` - Atomic operation

### Common Queries
```kotlin
// Basic CRUD
SELECT * FROM table
INSERT INTO table VALUES (...)
UPDATE table SET ... WHERE ...
DELETE FROM table WHERE ...

// Ordering
ORDER BY column ASC/DESC

// Limiting
LIMIT 10

// Filtering
WHERE column = value
WHERE column IN (value1, value2)
WHERE column LIKE '%pattern%'
WHERE column BETWEEN start AND end

// Aggregation
COUNT(*)
SUM(column)
AVG(column)
MAX(column)
MIN(column)

// Grouping
GROUP BY column
HAVING condition
```

---

This code reference provides quick access to all common patterns and code snippets you'll need when working with the SQLite chat history implementation. Keep it handy for quick lookups!
