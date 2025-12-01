# Testing Guide for SQLite Chat History

This guide provides comprehensive testing strategies for the chat history implementation.

## Quick Testing Checklist

### ✅ Manual Testing Steps

1. **Install Fresh App**
   - [ ] Clean install on device/emulator
   - [ ] Navigate to AI Assistant
   - [ ] Verify welcome message appears
   - [ ] Should see empty chat (except welcome)

2. **Send Messages**
   - [ ] Type a message
   - [ ] Click Send button
   - [ ] Message appears in chat
   - [ ] AI response appears
   - [ ] Messages have timestamps
   - [ ] User messages align right
   - [ ] AI messages align left

3. **Test Persistence**
   - [ ] Send 5-10 messages
   - [ ] Close app completely (swipe away from recents)
   - [ ] Reopen app
   - [ ] Navigate to AI Assistant
   - [ ] All messages still visible
   - [ ] Messages in correct order
   - [ ] Timestamps unchanged

4. **Clear History**
   - [ ] Click delete icon in toolbar
   - [ ] Confirmation dialog appears
   - [ ] Click "Clear"
   - [ ] All messages deleted
   - [ ] Only welcome message remains
   - [ ] Database is empty

5. **Multiple Sessions**
   - [ ] Send messages in session 1
   - [ ] Close app
   - [ ] Reopen app
   - [ ] Old messages still there
   - [ ] Send new messages
   - [ ] Both old and new messages visible
   - [ ] Correct chronological order

6. **Edge Cases**
   - [ ] Send empty message (should be disabled)
   - [ ] Send very long message (should handle)
   - [ ] Send special characters (emoji, symbols)
   - [ ] Rapid-fire messages (stress test)
   - [ ] Send while loading (should wait)

## Unit Testing

### 1. Testing ChatRepository

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatRepositoryTest {
    
    private lateinit var database: ChatDatabase
    private lateinit var chatDao: ChatMessageDao
    private lateinit var repository: ChatRepository
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).build()
        chatDao = database.chatMessageDao()
        repository = ChatRepository(chatDao)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insertMessage_retrievesMessage() = runBlocking {
        // Given
        val message = ChatMessageEntity(
            text = "Test message",
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        
        // When
        repository.insertMessage(message)
        
        // Then
        val messages = repository.allMessages.first()
        assertEquals(1, messages.size)
        assertEquals("Test message", messages[0].text)
        assertTrue(messages[0].isUser)
    }
    
    @Test
    fun deleteAllMessages_clearsDatabase() = runBlocking {
        // Given
        repository.insertMessage(ChatMessageEntity("Test 1", true))
        repository.insertMessage(ChatMessageEntity("Test 2", false))
        
        // When
        repository.deleteAllMessages()
        
        // Then
        val messages = repository.allMessages.first()
        assertEquals(0, messages.size)
    }
    
    @Test
    fun messagesOrderedByTimestamp() = runBlocking {
        // Given
        val message1 = ChatMessageEntity("First", true, 1000L)
        val message2 = ChatMessageEntity("Second", true, 2000L)
        val message3 = ChatMessageEntity("Third", true, 1500L)
        
        // When
        repository.insertMessage(message1)
        repository.insertMessage(message2)
        repository.insertMessage(message3)
        
        // Then
        val messages = repository.allMessages.first()
        assertEquals("First", messages[0].text)
        assertEquals("Third", messages[1].text)
        assertEquals("Second", messages[2].text)
    }
}
```

### 2. Testing ViewModel

```kotlin
@ExperimentalCoroutinesApi
class AiAssistantViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineRule = TestCoroutineRule()
    
    private lateinit var viewModel: AiAssistantViewModel
    private lateinit var repository: ChatRepository
    
    @Before
    fun setup() {
        // Mock repository
        repository = mockk<ChatRepository>()
        
        // Setup Flow
        every { repository.allMessages } returns flow { emit(emptyList()) }
        
        viewModel = AiAssistantViewModel(repository)
    }
    
    @Test
    fun `sendMessage saves user message to repository`() = runBlocking {
        // Given
        val userMessage = "Hello"
        coEvery { repository.insertMessage(any()) } just Runs
        
        // When
        viewModel.sendMessage(userMessage)
        
        // Then
        coVerify(exactly = 2) { // User message + AI response
            repository.insertMessage(any())
        }
    }
    
    @Test
    fun `sendMessage with blank text does nothing`() = runBlocking {
        // Given
        val blankMessage = "   "
        
        // When
        viewModel.sendMessage(blankMessage)
        
        // Then
        coVerify(exactly = 0) {
            repository.insertMessage(any())
        }
    }
    
    @Test
    fun `clearHistory calls repository deleteAllMessages`() = runBlocking {
        // Given
        coEvery { repository.deleteAllMessages() } just Runs
        
        // When
        viewModel.clearHistory()
        
        // Then
        coVerify { repository.deleteAllMessages() }
    }
    
    @Test
    fun `isLoading is true while sending message`() = runBlocking {
        // Given
        coEvery { repository.insertMessage(any()) } coAnswers {
            delay(100) // Simulate network delay
        }
        
        // When
        val job = launch {
            viewModel.sendMessage("Test")
        }
        
        // Then
        advanceTimeBy(50)
        assertTrue(viewModel.isLoading.value)
        
        advanceTimeBy(100)
        assertFalse(viewModel.isLoading.value)
        
        job.cancel()
    }
}
```

### 3. Testing DAO

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatMessageDaoTest {
    
    private lateinit var database: ChatDatabase
    private lateinit var chatDao: ChatMessageDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).allowMainThreadQueries()
        .build()
        chatDao = database.chatMessageDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveMessage() = runBlocking {
        // Given
        val message = ChatMessageEntity(
            text = "Test",
            isUser = true,
            timestamp = 12345L
        )
        
        // When
        chatDao.insertMessage(message)
        val messages = chatDao.getAllMessages().first()
        
        // Then
        assertEquals(1, messages.size)
        assertEquals("Test", messages[0].text)
    }
    
    @Test
    fun getMessagesBySession() = runBlocking {
        // Given
        val session1 = "session-1"
        val session2 = "session-2"
        
        chatDao.insertMessage(ChatMessageEntity("Msg 1", true, sessionId = session1))
        chatDao.insertMessage(ChatMessageEntity("Msg 2", true, sessionId = session2))
        chatDao.insertMessage(ChatMessageEntity("Msg 3", true, sessionId = session1))
        
        // When
        val session1Messages = chatDao.getMessagesBySession(session1).first()
        
        // Then
        assertEquals(2, session1Messages.size)
        assertTrue(session1Messages.all { it.sessionId == session1 })
    }
    
    @Test
    fun deleteMessage() = runBlocking {
        // Given
        val message = ChatMessageEntity("Test", true)
        chatDao.insertMessage(message)
        val inserted = chatDao.getAllMessages().first()[0]
        
        // When
        chatDao.deleteMessage(inserted)
        
        // Then
        val messages = chatDao.getAllMessages().first()
        assertEquals(0, messages.size)
    }
}
```

## Integration Testing

### Testing Complete Flow

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatFlowIntegrationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var database: ChatDatabase
    private lateinit var repository: ChatRepository
    private lateinit var viewModel: AiAssistantViewModel
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).build()
        
        repository = ChatRepository(database.chatMessageDao())
        viewModel = AiAssistantViewModel(repository)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun completeMessageFlow() = runBlocking {
        // Given
        composeTestRule.setContent {
            AiAssistantScreen(navController = rememberNavController())
        }
        
        // When - Send message
        composeTestRule.onNodeWithTag("messageInput")
            .performTextInput("Hello AI")
        composeTestRule.onNodeWithTag("sendButton")
            .performClick()
        
        // Then - Message appears
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Hello AI")
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        // And - AI response appears
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("AI Assistant")
                .fetchSemanticsNodes().size >= 2 // Welcome + response
        }
    }
}
```

## Performance Testing

### 1. Load Testing

```kotlin
@Test
fun `handle 1000 messages efficiently`() = runBlocking {
    // Given
    val messages = (1..1000).map { i ->
        ChatMessageEntity(
            text = "Message $i",
            isUser = i % 2 == 0,
            timestamp = i.toLong()
        )
    }
    
    // When - Measure time to insert
    val insertTime = measureTimeMillis {
        messages.forEach { repository.insertMessage(it) }
    }
    
    // Then - Should be reasonably fast
    assertTrue("Insert took too long: $insertTime ms", insertTime < 5000)
    
    // When - Measure time to retrieve
    val retrieveTime = measureTimeMillis {
        repository.allMessages.first()
    }
    
    // Then
    assertTrue("Retrieve took too long: $retrieveTime ms", retrieveTime < 1000)
}
```

### 2. Memory Testing

```kotlin
@Test
fun `verify no memory leaks with Flow collection`() = runBlocking {
    // Given
    val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    
    // When - Collect messages multiple times
    repeat(100) {
        val job = launch {
            repository.allMessages.collect { messages ->
                // Simulate processing
                messages.size
            }
        }
        delay(10)
        job.cancel()
    }
    
    // Force garbage collection
    System.gc()
    delay(1000)
    
    // Then - Memory shouldn't increase significantly
    val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    val memoryIncrease = (finalMemory - initialMemory) / 1024 / 1024 // MB
    
    assertTrue("Memory leak detected: ${memoryIncrease}MB increase", memoryIncrease < 10)
}
```

## UI Testing with Espresso

```kotlin
@RunWith(AndroidJUnit4::class)
class AiAssistantScreenTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun sendMessage_displaysInChatList() {
        // Navigate to AI Assistant
        onView(withText("AI Assistant")).perform(click())
        
        // Type message
        onView(withHint("Ask me anything..."))
            .perform(typeText("Test message"), closeSoftKeyboard())
        
        // Send message
        onView(withText("Send")).perform(click())
        
        // Verify message appears
        onView(withText("Test message"))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun clearHistory_showsConfirmationDialog() {
        // Navigate to AI Assistant
        onView(withText("AI Assistant")).perform(click())
        
        // Click delete icon
        onView(withContentDescription("Clear History"))
            .perform(click())
        
        // Verify dialog appears
        onView(withText("Clear Chat History"))
            .check(matches(isDisplayed()))
        
        onView(withText("Are you sure you want to delete all chat messages?"))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun clearHistory_removesAllMessages() {
        // Navigate and send message
        onView(withText("AI Assistant")).perform(click())
        onView(withHint("Ask me anything..."))
            .perform(typeText("Test"), closeSoftKeyboard())
        onView(withText("Send")).perform(click())
        
        // Clear history
        onView(withContentDescription("Clear History")).perform(click())
        onView(withText("Clear")).perform(click())
        
        // Verify message removed
        onView(withText("Test"))
            .check(doesNotExist())
    }
}
```

## Database Inspector (Manual Testing)

### Using Android Studio Database Inspector:

1. **Open Database Inspector**
   - Run app on device/emulator
   - View > Tool Windows > App Inspection
   - Select "Database Inspector" tab
   - Choose your app process

2. **Inspect Tables**
   - Expand "chat_database"
   - Click "chat_messages" table
   - View all rows in table

3. **Run Queries**
   ```sql
   -- View all messages
   SELECT * FROM chat_messages ORDER BY timestamp DESC
   
   -- Count messages
   SELECT COUNT(*) as total FROM chat_messages
   
   -- Messages by session
   SELECT sessionId, COUNT(*) as count 
   FROM chat_messages 
   GROUP BY sessionId
   
   -- Recent user messages
   SELECT * FROM chat_messages 
   WHERE isUser = 1 
   ORDER BY timestamp DESC 
   LIMIT 10
   ```

4. **Modify Data**
   - Double-click cell to edit
   - Test how UI handles changes
   - Verify Flow emits updates

## Debugging Tips

### Enable Room Logging

```kotlin
// In ChatDatabase.kt
fun getDatabase(context: Context): ChatDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        ChatDatabase::class.java,
        "chat_database"
    )
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL: $sqlQuery, Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
}
```

### Log Flow Emissions

```kotlin
// In ViewModel
private fun loadMessages() {
    viewModelScope.launch {
        chatRepository.allMessages
            .onEach { messages ->
                Log.d("AiViewModel", "Received ${messages.size} messages")
            }
            .collect { messageList ->
                _messages.value = messageList
            }
    }
}
```

### Check Database File Directly

```bash
# Connect to device
adb shell

# Navigate to database
cd /data/data/com.example.democse3310/databases

# Open with sqlite3
sqlite3 chat_database

# Run queries
.tables
SELECT * FROM chat_messages;
.quit
```

## Test Coverage Goals

- [ ] **Unit Tests**: 80%+ coverage
- [ ] **Integration Tests**: All critical paths
- [ ] **UI Tests**: All user interactions
- [ ] **Performance Tests**: No memory leaks
- [ ] **Edge Cases**: All error scenarios

## Common Test Scenarios

### Scenario 1: Fresh Install
- Install app
- Open AI Assistant
- Verify welcome message
- Database created successfully

### Scenario 2: Rapid Messages
- Send 10 messages quickly
- All saved to database
- UI doesn't lag
- Correct order maintained

### Scenario 3: App Crash Recovery
- Send messages
- Force stop app (adb shell am force-stop com.example.democse3310)
- Restart app
- All messages preserved

### Scenario 4: Low Storage
- Fill device storage (95%+)
- Try to save message
- Handle error gracefully
- Show user-friendly message

### Scenario 5: Concurrent Access
- Multiple screens accessing database
- No race conditions
- Data consistency maintained

## Continuous Integration

### Add to CI/CD Pipeline:

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK
      uses: actions/setup-java@v2
      with:
        java-version: '17'
    
    - name: Run Unit Tests
      run: ./gradlew testDebugUnitTest
    
    - name: Run Instrumented Tests
      run: ./gradlew connectedDebugAndroidTest
    
    - name: Upload Test Reports
      uses: actions/upload-artifact@v2
      with:
        name: test-reports
        path: app/build/reports/tests/
```

## Summary

This testing guide covers:
- ✅ Manual testing procedures
- ✅ Unit tests for each component
- ✅ Integration tests for complete flows
- ✅ Performance and memory testing
- ✅ UI testing with Compose/Espresso
- ✅ Database inspection tools
- ✅ Debugging techniques
- ✅ CI/CD integration

Use these tests to ensure your chat history implementation is robust, performant, and bug-free.
