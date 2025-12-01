# Troubleshooting Guide - SQLite Chat History

## Common Issues and Solutions

### 🔴 Build/Compilation Issues

#### Issue 1: "Unresolved reference: room"

**Symptoms:**
```
Unresolved reference: Entity
Unresolved reference: Dao
Unresolved reference: Database
```

**Solution:**
1. Check `build.gradle.kts` has Room dependencies:
```kotlin
val roomVersion = "2.6.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")
```

2. Ensure KSP plugin is added:
```kotlin
id("com.google.devtools.ksp") version "2.0.21-1.0.25"
```

3. Sync Gradle: File > Sync Project with Gradle Files
4. Clean build: Build > Clean Project, then Build > Rebuild Project

---

#### Issue 2: "Cannot access database on main thread"

**Error:**
```
Cannot access database on the main thread since it may potentially lock the UI for a long period of time
```

**Solution:**
All database operations are already wrapped in `suspend` functions and use coroutines. If you see this error:

1. Check you're using `viewModelScope.launch`:
```kotlin
// ✅ Correct
viewModelScope.launch {
    repository.insertMessage(message)
}

// ❌ Wrong
repository.insertMessage(message)  // Crashes!
```

2. For debugging only, you can allow main thread queries:
```kotlin
Room.databaseBuilder(...)
    .allowMainThreadQueries()  // Only for testing!
    .build()
```

---

#### Issue 3: KSP not generating code

**Symptoms:**
- Room classes show errors
- DAO implementation missing
- "Cannot find implementation for..."

**Solution:**
1. Clean and rebuild:
```bash
./gradlew clean
./gradlew build
```

2. Check KSP configuration in `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}
```

3. Sync project
4. Invalidate caches: File > Invalidate Caches > Invalidate and Restart

---

### 🔴 Runtime Issues

#### Issue 4: App crashes when opening AI Assistant

**Error in Logcat:**
```
java.lang.RuntimeException: Cannot create an instance of class AiAssistantViewModel
```

**Solution:**
Ensure you're using the ViewModelFactory:

```kotlin
// ✅ Correct
val viewModel: AiAssistantViewModel = viewModel(
    factory = AiAssistantViewModelFactory(repository)
)

// ❌ Wrong
val viewModel: AiAssistantViewModel = viewModel()  // Crashes!
```

---

#### Issue 5: Messages not persisting

**Symptoms:**
- Messages disappear after closing app
- Database seems empty after restart

**Solution:**

1. Verify database creation:
```kotlin
val database = remember { ChatDatabase.getDatabase(context) }
```

2. Check database file exists:
```bash
adb shell
cd /data/data/com.example.democse3310/databases
ls -la
# Should see: chat_database
```

3. Verify inserts are completing:
```kotlin
viewModelScope.launch {
    try {
        repository.insertMessage(message)
        Log.d("Chat", "Message saved successfully")
    } catch (e: Exception) {
        Log.e("Chat", "Failed to save message", e)
    }
}
```

4. Check if you're using destructive migration (loses data):
```kotlin
// In ChatDatabase.kt
.fallbackToDestructiveMigration()  // ⚠️ Deletes data on schema change!
```

---

#### Issue 6: UI not updating when messages added

**Symptoms:**
- Database has messages (verified in Database Inspector)
- UI shows empty or old messages
- Manual refresh needed

**Solution:**

1. Verify Flow collection in ViewModel:
```kotlin
private fun loadMessages() {
    viewModelScope.launch {
        chatRepository.allMessages.collect { messageList ->
            _messages.value = messageList  // Must update StateFlow
        }
    }
}
```

2. Ensure StateFlow is collected in UI:
```kotlin
val messages by viewModel.messages.collectAsState()  // Must use 'by'
```

3. Check ViewModel is not recreated:
```kotlin
// Use remember to maintain ViewModel across recompositions
val viewModel: AiAssistantViewModel = viewModel(
    factory = AiAssistantViewModelFactory(repository)
)
```

---

#### Issue 7: "Flow invariant is violated"

**Error:**
```
java.lang.IllegalStateException: Flow invariant is violated
```

**Solution:**
This happens when collecting a Flow multiple times. In ViewModel:

```kotlin
// ✅ Correct - collect once in init
init {
    loadMessages()
}

private fun loadMessages() {
    viewModelScope.launch {
        chatRepository.allMessages.collect { ... }
    }
}

// ❌ Wrong - don't collect on every access
val messages: StateFlow<List<ChatMessageEntity>> = 
    chatRepository.allMessages.stateIn(...)  // Causes issues
```

---

### 🔴 Database Issues

#### Issue 8: Database schema migration error

**Error:**
```
A migration from X to Y was required but not found
```

**Solution:**

1. For development, use destructive migration:
```kotlin
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration()
    .build()
```

2. For production, provide proper migration:
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE chat_messages ADD COLUMN newColumn TEXT")
    }
}

Room.databaseBuilder(...)
    .addMigrations(MIGRATION_1_2)
    .build()
```

3. Or uninstall and reinstall app (loses data):
```bash
adb uninstall com.example.democse3310
# Then reinstall from Android Studio
```

---

#### Issue 9: Database corrupted

**Error:**
```
SQLiteDatabaseCorruptException: database disk image is malformed
```

**Solution:**

1. Clear app data:
```bash
adb shell pm clear com.example.democse3310
```

2. Or delete database manually:
```bash
adb shell
cd /data/data/com.example.democse3310/databases
rm chat_database*
```

3. Add database deletion in code (for testing):
```kotlin
context.deleteDatabase("chat_database")
```

---

#### Issue 10: Query returns null or empty

**Symptoms:**
- `getAllMessages()` returns empty list
- Database Inspector shows data exists

**Solution:**

1. Check query syntax:
```kotlin
@Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
fun getAllMessages(): Flow<List<ChatMessageEntity>>
```

2. Verify table name matches entity:
```kotlin
@Entity(tableName = "chat_messages")  // Must match query
```

3. Check if Flow is being collected:
```kotlin
repository.allMessages.collect { messages ->
    Log.d("Chat", "Received ${messages.size} messages")
}
```

---

### 🔴 Performance Issues

#### Issue 11: App becomes slow with many messages

**Symptoms:**
- UI lag when scrolling
- Database operations take long time
- App uses too much memory

**Solution:**

1. Add database indices:
```kotlin
@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["sessionId"])
    ]
)
```

2. Implement pagination:
```kotlin
@Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
fun getMessagesPage(limit: Int, offset: Int): Flow<List<ChatMessageEntity>>
```

3. Use Paging 3 library:
```kotlin
@Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
fun getPagedMessages(): PagingSource<Int, ChatMessageEntity>
```

4. Delete old messages:
```kotlin
@Query("DELETE FROM chat_messages WHERE timestamp < :cutoffTime")
suspend fun deleteOldMessages(cutoffTime: Long)

// Call monthly
val oneMonthAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
chatDao.deleteOldMessages(oneMonthAgo)
```

---

#### Issue 12: Memory leaks with Flow

**Symptoms:**
- Memory usage increases over time
- App crashes with OutOfMemoryError

**Solution:**

1. Cancel coroutines properly:
```kotlin
// Use viewModelScope - automatically cancels
viewModelScope.launch {
    repository.allMessages.collect { ... }
}
```

2. Don't collect Flow in Composable repeatedly:
```kotlin
// ✅ Correct
val messages by viewModel.messages.collectAsState()

// ❌ Wrong - creates new collection on every recomposition
LaunchedEffect(Unit) {
    viewModel.messages.collect { ... }
}
```

---

### 🔴 UI Issues

#### Issue 13: Keyboard pushes content off screen

**Solution:**

Use `imePadding()` modifier:
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .imePadding()  // Adjusts for keyboard
) {
    // Your content
}
```

---

#### Issue 14: List doesn't auto-scroll to bottom

**Solution:**

```kotlin
val listState = rememberLazyListState()

LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
        listState.animateScrollToItem(messages.size - 1)
    }
}

LazyColumn(state = listState) {
    // Your items
}
```

---

#### Issue 15: Loading indicator doesn't show

**Solution:**

1. Check isLoading is being set:
```kotlin
viewModelScope.launch {
    _isLoading.value = true
    try {
        // Do work
    } finally {
        _isLoading.value = false  // Always reset
    }
}
```

2. Collect in UI:
```kotlin
val isLoading by viewModel.isLoading.collectAsState()

if (isLoading) {
    CircularProgressIndicator()
}
```

---

### 🔴 Testing Issues

#### Issue 16: Tests fail with "Cannot create database"

**Solution:**

Use in-memory database for tests:
```kotlin
@Before
fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(
        context,
        ChatDatabase::class.java
    ).allowMainThreadQueries()  // OK for tests
    .build()
}
```

---

#### Issue 17: Tests timeout

**Solution:**

1. Use `runBlocking` for suspend functions:
```kotlin
@Test
fun testDatabase() = runBlocking {
    val message = ChatMessageEntity("Test", true)
    chatDao.insertMessage(message)
    
    val result = chatDao.getAllMessages().first()
    assertEquals(1, result.size)
}
```

2. Set proper timeout:
```kotlin
@Test(timeout = 5000)  // 5 second timeout
fun testLongOperation() = runBlocking {
    // Test code
}
```

---

## Debugging Tools

### 1. Enable Room Logging

```kotlin
// In ChatDatabase.kt
.setQueryCallback({ sqlQuery, bindArgs ->
    Log.d("RoomSQL", "Query: $sqlQuery")
    Log.d("RoomSQL", "Args: $bindArgs")
}, Executors.newSingleThreadExecutor())
```

### 2. Add Debug Logging

```kotlin
// In ViewModel
private fun loadMessages() {
    viewModelScope.launch {
        Log.d("Chat", "Starting to collect messages")
        chatRepository.allMessages.collect { messageList ->
            Log.d("Chat", "Received ${messageList.size} messages")
            _messages.value = messageList
        }
    }
}
```

### 3. Database Inspector

1. Run app
2. View > Tool Windows > App Inspection
3. Database Inspector tab
4. Inspect tables in real-time

### 4. Profiler

1. View > Tool Windows > Profiler
2. Monitor memory usage
3. Check for leaks
4. Analyze database queries

### 5. Logcat Filters

```
// View only your app logs
package:com.example.democse3310

// View Room logs
tag:RoomSQL

// View ViewModel logs
tag:AiViewModel
```

---

## Quick Fixes

### Clean Slate (Nuclear Option)

If nothing works:

```bash
# 1. Clean build
./gradlew clean

# 2. Delete app from device
adb uninstall com.example.democse3310

# 3. Invalidate caches in Android Studio
File > Invalidate Caches > Invalidate and Restart

# 4. Delete .gradle folder
rm -rf .gradle/

# 5. Rebuild
./gradlew build
```

---

## Prevention Tips

### 1. Always Use Try-Catch

```kotlin
viewModelScope.launch {
    try {
        repository.insertMessage(message)
    } catch (e: Exception) {
        Log.e("Chat", "Error saving message", e)
        _error.value = "Failed to save message"
    }
}
```

### 2. Validate Input

```kotlin
fun sendMessage(text: String) {
    if (text.isBlank()) return
    if (text.length > 5000) {
        _error.value = "Message too long"
        return
    }
    // Process message
}
```

### 3. Handle Edge Cases

```kotlin
// Handle empty list
if (messages.isEmpty()) {
    Text("No messages yet")
} else {
    LazyColumn { ... }
}

// Handle errors
when (val state = uiState) {
    is Success -> ShowMessages(state.data)
    is Error -> ShowError(state.message)
    is Loading -> ShowLoading()
}
```

### 4. Add Logging

```kotlin
// Log important events
Log.d("Chat", "User sent message: ${text.take(50)}...")
Log.d("Chat", "AI response generated")
Log.e("Chat", "Database error", exception)
```

### 5. Write Tests

- Unit tests catch issues early
- Integration tests verify complete flows
- UI tests ensure user experience

---

## Getting Help

If you're still stuck:

1. **Check Logcat** - Most errors show up there
2. **Use Database Inspector** - Verify data is actually saved
3. **Search Stack Overflow** - Most issues already solved
4. **Read Room Documentation** - Official Android docs
5. **Debug Step-by-Step** - Use breakpoints to trace execution

### Useful Links

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Compose State Guide](https://developer.android.com/jetpack/compose/state)
- [Database Inspector Guide](https://developer.android.com/studio/inspect/database)

---

## Summary

Most issues fall into these categories:
1. **Build issues** → Sync Gradle, clean/rebuild
2. **Runtime crashes** → Check ViewModelFactory, coroutines
3. **Data not persisting** → Verify database creation, check migrations
4. **UI not updating** → Ensure Flow collection, StateFlow usage
5. **Performance problems** → Add indices, implement pagination

Always check Logcat first - it usually tells you exactly what's wrong!
