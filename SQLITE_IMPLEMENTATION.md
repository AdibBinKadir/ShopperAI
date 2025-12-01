# SQLite Chat History Implementation for ShopperAI

## Overview
This implementation adds persistent chat history storage using Room (SQLite) for the AI Assistant chatbot feature in ShopperAI.

## Architecture

### Database Layer
```
ChatDatabase (Room Database)
    ↓
ChatMessageDao (Data Access Object)
    ↓
ChatMessageEntity (Table)
```

### Components Created

#### 1. **ChatMessageEntity.kt** - Database Entity
- Represents a single chat message in the database
- Fields:
  - `id`: Auto-generated primary key
  - `text`: Message content
  - `isUser`: Boolean flag (true for user, false for AI)
  - `timestamp`: Message timestamp in milliseconds
  - `sessionId`: Optional session grouping identifier

#### 2. **ChatMessageDao.kt** - Data Access Object
- Interface for database operations
- Methods:
  - `getAllMessages()`: Returns Flow of all messages ordered by timestamp
  - `getMessagesBySession(sessionId)`: Get messages for specific session
  - `insertMessage(message)`: Insert new message
  - `deleteAllMessages()`: Clear entire chat history
  - `deleteSession(sessionId)`: Delete specific session
  - `deleteMessage(message)`: Delete individual message

#### 3. **ChatDatabase.kt** - Room Database
- Singleton database instance
- Version 1
- Provides ChatMessageDao instance
- Uses destructive migration for simplicity

#### 4. **ChatRepository.kt** - Repository Pattern
- Abstracts data access from ViewModel
- Provides clean API for chat operations
- Exposes Flow<List<ChatMessageEntity>> for reactive updates

#### 5. **AiAssistantViewModel.kt** - Updated ViewModel
- Uses ChatRepository for database operations
- Manages chat state with StateFlow
- Features:
  - `messages`: StateFlow of all messages
  - `isLoading`: Loading state indicator
  - `sendMessage(text)`: Send user message and get AI response
  - `clearHistory()`: Delete all messages
  - `addWelcomeMessage()`: Add initial greeting
  - Session management with UUID

#### 6. **AiAssistantViewModelFactory.kt** - ViewModel Factory
- Injects ChatRepository into ViewModel
- Required for proper ViewModel initialization with dependencies

#### 7. **AiAssistantScreen.kt** - Updated UI
- Initializes database and repository
- Creates ViewModel with factory
- Features:
  - Real-time message display from database
  - Loading indicator during AI response
  - Timestamp display for each message
  - Clear history button with confirmation dialog
  - Auto-scroll to newest messages
  - Improved message bubbles with time stamps

## Database Schema

```sql
CREATE TABLE chat_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    text TEXT NOT NULL,
    isUser INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    sessionId TEXT NOT NULL
);
```

## Data Flow

### Sending a Message:
1. User types message and clicks "Send"
2. `AiAssistantScreen` calls `viewModel.sendMessage(text)`
3. ViewModel saves user message to database via ChatRepository
4. ViewModel generates AI response (currently mock, ready for Gemini API)
5. ViewModel saves AI response to database
6. Database emits updated list via Flow
7. UI automatically updates with new messages

### Loading Messages:
1. Screen initializes with database and repository
2. ViewModel subscribes to `chatRepository.allMessages` Flow
3. Database changes automatically propagate to UI
4. Messages persist across app restarts

## Key Features

### ✅ Persistent Storage
- All chat messages saved to SQLite database
- Data survives app restarts and crashes
- Efficient querying with Room

### ✅ Reactive Updates
- Uses Kotlin Flow for real-time updates
- UI automatically reflects database changes
- No manual refresh needed

### ✅ Session Management
- Each conversation has unique session ID
- Can filter messages by session
- Easy to implement conversation history view

### ✅ Clean Architecture
- Separation of concerns (Entity, DAO, Repository, ViewModel, UI)
- Easy to test each component
- Scalable for future features

### ✅ User-Friendly Features
- Clear history with confirmation dialog
- Timestamps on all messages
- Loading indicator during AI processing
- Auto-scroll to newest messages
- Visual distinction between user and AI messages

## Build Configuration

### Dependencies Added to `build.gradle.kts`:
```kotlin
// Room Database for SQLite
val roomVersion = "2.6.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")

// Coroutines (if not already present)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### Plugin Added:
```kotlin
id("com.google.devtools.ksp") version "2.0.21-1.0.25"
```

## Usage

### Basic Usage:
The implementation is already integrated into `AiAssistantScreen`. When users navigate to the AI Assistant:
1. Welcome message appears automatically
2. All previous messages load from database
3. Users can chat normally
4. History persists forever (until cleared)

### Clearing History:
- Tap the delete icon in the top bar
- Confirm deletion in dialog
- All messages removed from database

### Future Enhancements:
1. **Connect Gemini API**: Replace mock responses in `generateAiResponse()` with actual API calls
2. **Session History View**: Create screen to browse past conversations
3. **Search Messages**: Add search functionality across all messages
4. **Export History**: Allow users to export chat history
5. **Message Editing**: Allow users to edit/delete individual messages
6. **Conversation Branching**: Support multiple conversation threads

## Testing the Implementation

### 1. Sync Gradle:
- Click "Sync Now" when prompted after updating build.gradle.kts
- Wait for dependencies to download

### 2. Build and Run:
- Click "Run" or use Shift+F10
- Navigate to AI Assistant screen
- Send a few messages

### 3. Verify Persistence:
- Close the app completely
- Reopen the app
- Navigate to AI Assistant
- Previous messages should still be visible

### 4. Test Clear History:
- Tap delete icon
- Confirm deletion
- All messages should disappear
- Database should be empty

## File Structure
```
app/src/main/java/com/example/democse3310/
├── data/
│   ├── ChatMessageEntity.kt       # Database entity
│   ├── ChatMessageDao.kt          # Data access object
│   ├── ChatDatabase.kt            # Room database
│   ├── Product.kt                 # Existing
│   └── User.kt                    # Existing
├── repository/
│   ├── ChatRepository.kt          # Chat data repository
│   └── GenerativeAiRepository.kt  # Existing
├── viewmodel/
│   ├── AiAssistantViewModel.kt    # Updated with database
│   ├── AiAssistantViewModelFactory.kt  # New factory
│   └── TextSearchViewModel.kt     # Existing
└── AiAssistantScreen.kt           # Updated UI
```

## Performance Considerations

- **Room uses SQLite**: Industry-standard, highly optimized
- **Flow is efficient**: Only emits when data changes
- **Indexed queries**: Primary key automatically indexed
- **Lazy loading**: Can implement pagination if needed
- **Background operations**: All database operations on IO thread via coroutines

## Security Considerations

- Database file stored in app's private directory
- Not accessible to other apps without root
- Consider encryption for sensitive data (Room supports encryption libraries)
- Implement user authentication before accessing history

## Troubleshooting

### Build Errors:
1. Ensure KSP plugin is properly added
2. Sync Gradle files
3. Clean and rebuild project
4. Invalidate caches if needed

### Runtime Errors:
1. Check logcat for specific Room errors
2. Verify database migrations if schema changes
3. Ensure proper context is passed to database

### No Messages Displayed:
1. Check if database is properly initialized
2. Verify Flow collection in ViewModel
3. Ensure UI is collecting StateFlow correctly

## Next Steps

1. **Sync Gradle** and let dependencies download
2. **Build the project** to verify everything compiles
3. **Test the app** to ensure chat history persists
4. **Connect Gemini API** in `generateAiResponse()` method
5. **Add more features** like search, export, etc.

## Summary

This implementation provides a robust, scalable foundation for chat history in ShopperAI. It follows Android best practices with Room, uses reactive programming with Flow, and maintains clean architecture principles. The chat history now persists across app sessions and provides a solid base for future AI assistant enhancements.
