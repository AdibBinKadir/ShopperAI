# SQLite Chat History Architecture Diagram

## Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                          │
│                     (AiAssistantScreen.kt)                      │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │ Text Input   │  │ Send Button  │  │ Clear Button │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │           LazyColumn (Message List)                     │  │
│  │  ┌────────────────────────────────────────────┐        │  │
│  │  │ ChatMessageBubble (User Message)           │        │  │
│  │  └────────────────────────────────────────────┘        │  │
│  │  ┌────────────────────────────────────────────┐        │  │
│  │  │ ChatMessageBubble (AI Response)            │        │  │
│  │  └────────────────────────────────────────────┘        │  │
│  └─────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ collectAsState()
                        │ viewModel.sendMessage()
                        │ viewModel.clearHistory()
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      VIEW MODEL LAYER                           │
│                  (AiAssistantViewModel.kt)                      │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ StateFlow<List<ChatMessageEntity>> messages              │  │
│  │ StateFlow<Boolean> isLoading                             │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Functions:                                               │  │
│  │  • sendMessage(text: String)                            │  │
│  │  • clearHistory()                                       │  │
│  │  • addWelcomeMessage()                                  │  │
│  │  • generateAiResponse(message: String)                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ Repository methods
                        │ insertMessage()
                        │ deleteAllMessages()
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER                            │
│                    (ChatRepository.kt)                          │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Flow<List<ChatMessageEntity>> allMessages                │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Abstraction layer between ViewModel and Database        │  │
│  │  • Clean API for data operations                        │  │
│  │  • Can add caching/business logic here                  │  │
│  │  • Makes testing easier                                 │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ DAO methods
                        │ getAllMessages()
                        │ insertMessage()
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA ACCESS LAYER                          │
│                     (ChatMessageDao.kt)                         │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ @Query("SELECT * FROM chat_messages ORDER BY...")       │  │
│  │ fun getAllMessages(): Flow<List<ChatMessageEntity>>     │  │
│  │                                                          │  │
│  │ @Insert                                                  │  │
│  │ suspend fun insertMessage(message: ChatMessageEntity)   │  │
│  │                                                          │  │
│  │ @Query("DELETE FROM chat_messages")                     │  │
│  │ suspend fun deleteAllMessages()                          │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ SQL queries
                        │ CRUD operations
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                            │
│                      (ChatDatabase.kt)                          │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Room Database (Singleton)                                │  │
│  │  • Thread-safe instance management                       │  │
│  │  • Database versioning                                   │  │
│  │  • Migration handling                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ SQLite operations
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SQLITE DATABASE FILE                        │
│                     (chat_database.db)                          │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Table: chat_messages                                     │  │
│  │ ┌──────────┬────────────┬──────────┬─────────────────┐  │  │
│  │ │    id    │    text    │  isUser  │   timestamp     │  │  │
│  │ ├──────────┼────────────┼──────────┼─────────────────┤  │  │
│  │ │    1     │ "Hello"    │   true   │ 1701234567890   │  │  │
│  │ │    2     │ "Hi there" │   false  │ 1701234567891   │  │  │
│  │ │    3     │ "Help me"  │   true   │ 1701234567892   │  │  │
│  │ └──────────┴────────────┴──────────┴─────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  Stored at: /data/data/com.example.democse3310/databases/      │
└─────────────────────────────────────────────────────────────────┘
```

## Message Flow Sequence

### Sending a Message

```
User                UI              ViewModel          Repository         DAO            Database
 │                   │                   │                   │               │                │
 │ 1. Types message  │                   │                   │               │                │
 │ 2. Clicks Send    │                   │                   │               │                │
 │──────────────────>│                   │                   │               │                │
 │                   │ 3. sendMessage()  │                   │               │                │
 │                   │──────────────────>│                   │               │                │
 │                   │                   │ 4. insertMessage()│               │                │
 │                   │                   │──────────────────>│               │                │
 │                   │                   │                   │ 5. INSERT SQL │                │
 │                   │                   │                   │──────────────>│                │
 │                   │                   │                   │               │ 6. Write to DB │
 │                   │                   │                   │               │───────────────>│
 │                   │                   │                   │               │<───────────────│
 │                   │                   │                   │<──────────────│                │
 │                   │                   │<──────────────────│                               │
 │                   │                   │                   │                               │
 │                   │                   │ 7. Generate AI    │                               │
 │                   │                   │    Response       │                               │
 │                   │                   │─────────┐         │                               │
 │                   │                   │         │         │                               │
 │                   │                   │<────────┘         │                               │
 │                   │                   │                   │                               │
 │                   │                   │ 8. insertMessage()│                               │
 │                   │                   │──────────────────>│                               │
 │                   │                   │                   │ 9. INSERT SQL                 │
 │                   │                   │                   │──────────────>│               │
 │                   │                   │                   │               │ 10. Write AI  │
 │                   │                   │                   │               │───────────────>│
 │                   │                   │                   │               │                │
 │                   │                   │                   │               │ 11. Emit Flow │
 │                   │<──────────────────────────────────────────────────────────────────────│
 │                   │ 12. UI Recomposes │                   │               │                │
 │<──────────────────│                   │                   │               │                │
 │ 13. Sees new msgs │                   │                   │               │                │
```

## State Management Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    StateFlow Pattern                        │
└─────────────────────────────────────────────────────────────┘

Database Change → Flow Emission → StateFlow Update → UI Recomposition

┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   Database   │      │  Repository  │      │  ViewModel   │
│   Changes    │─────>│    Flow      │─────>│  StateFlow   │
└──────────────┘      └──────────────┘      └──────┬───────┘
                                                    │
                                                    │
                                                    ▼
                                            ┌──────────────┐
                                            │ UI Collects  │
                                            │   State      │
                                            └──────────────┘
                                                    │
                                                    │
                                                    ▼
                                            ┌──────────────┐
                                            │ Composable   │
                                            │ Recomposes   │
                                            └──────────────┘
```

## Dependency Injection Pattern

```
┌──────────────────────────────────────────────────────────────┐
│                     Composable Screen                        │
│                  (AiAssistantScreen)                         │
│                                                              │
│  val context = LocalContext.current                         │
│  val database = ChatDatabase.getDatabase(context)           │
│  val repository = ChatRepository(database.chatMessageDao()) │
│  val viewModel = viewModel(                                 │
│      factory = AiAssistantViewModelFactory(repository)      │
│  )                                                           │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ Creates instances
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                  Dependency Tree                             │
│                                                              │
│  Context                                                     │
│    └── ChatDatabase (Singleton)                             │
│          └── ChatMessageDao                                 │
│                └── ChatRepository                           │
│                      └── AiAssistantViewModel               │
└──────────────────────────────────────────────────────────────┘
```

## Thread Management

```
┌─────────────────────────────────────────────────────────────┐
│                      Thread Diagram                         │
└─────────────────────────────────────────────────────────────┘

Main Thread (UI)               IO Thread (Database)
     │                               │
     │ User Interaction              │
     │───────────┐                   │
     │           │                   │
     │ viewModelScope.launch {       │
     │     ──────────────────────────>│
     │                               │ Database Operation
     │                               │ (Coroutine)
     │                               │
     │     <──────────────────────────│
     │ }                             │
     │                               │
     │ Flow.collect()                │
     │<──────────────────────────────│
     │                               │
     │ UI Updates                    │
     │───────────┐                   │
     │           │                   │
```

## Error Handling Flow

```
┌────────────────────────────────────────────────────────────┐
│              Error Handling Strategy                       │
└────────────────────────────────────────────────────────────┘

Try-Catch in ViewModel:

viewModelScope.launch {
    try {
        // Database operation
        repository.insertMessage(message)
        _status.value = Success
    } catch (e: SQLiteException) {
        _status.value = Error("Database error")
        Log.e(TAG, "DB Error", e)
    } catch (e: Exception) {
        _status.value = Error("Unknown error")
        Log.e(TAG, "Error", e)
    }
}

UI Observes Status:

when (status) {
    is Success -> Show success message
    is Error -> Show error dialog
    is Loading -> Show progress bar
}
```

## File Structure Visualization

```
ShopperAIoff/
└── app/
    └── src/
        └── main/
            └── java/
                └── com/
                    └── example/
                        └── democse3310/
                            │
                            ├── data/                    [Data Layer]
                            │   ├── ChatMessageEntity.kt    ← Table schema
                            │   ├── ChatMessageDao.kt       ← SQL operations
                            │   ├── ChatDatabase.kt         ← DB instance
                            │   ├── Product.kt
                            │   └── User.kt
                            │
                            ├── repository/              [Repository Layer]
                            │   ├── ChatRepository.kt       ← Data abstraction
                            │   └── GenerativeAiRepository.kt
                            │
                            ├── viewmodel/               [ViewModel Layer]
                            │   ├── AiAssistantViewModel.kt    ← State management
                            │   ├── AiAssistantViewModelFactory.kt
                            │   └── TextSearchViewModel.kt
                            │
                            └── AiAssistantScreen.kt     [UI Layer]
                                                         ← User interface
```

## Benefits of This Architecture

```
┌────────────────────────────────────────────────────────┐
│                   Architecture Benefits                │
└────────────────────────────────────────────────────────┘

1. SEPARATION OF CONCERNS
   ├── UI only handles display
   ├── ViewModel manages state
   ├── Repository abstracts data
   └── DAO handles SQL

2. TESTABILITY
   ├── Mock DAO for Repository tests
   ├── Mock Repository for ViewModel tests
   ├── Mock ViewModel for UI tests
   └── Unit test each layer independently

3. MAINTAINABILITY
   ├── Changes isolated to single layer
   ├── Easy to swap implementations
   ├── Clear data flow
   └── Documented interfaces

4. SCALABILITY
   ├── Add features without breaking existing
   ├── Easy to add caching layer
   ├── Can add offline sync
   └── Support multiple data sources

5. REACTIVE UPDATES
   ├── Flow automatically updates UI
   ├── No manual refresh needed
   ├── Thread-safe updates
   └── Efficient recomposition
```

## Summary

This architecture follows Android best practices:
- **Clean Architecture** with clear layer separation
- **MVVM Pattern** for UI and business logic separation
- **Repository Pattern** for data abstraction
- **Room Database** for reliable SQLite access
- **Kotlin Coroutines** for async operations
- **StateFlow** for reactive state management
- **Dependency Injection** (manual) for loose coupling

The result is a maintainable, testable, and scalable chat history system.
