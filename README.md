# ShopperAI - AI-Powered Shopping Assistant

**Team 12 - CSE 3310, Fall 2025**

## Project Overview

ShopperAI is an Android application that helps users find products and compare prices using AI-powered search capabilities. The app provides three main search methods:

1. **Text-Based Search** - Search for products by name or description
2. **Reverse Image Search** - Upload or capture images to find similar products
3. **AI Assistant Chat** - Conversational interface with persistent chat history

## Features

### ✅ Implemented
- **User Authentication**
  - Registration with full validation (name, email, phone, user ID, password, security Q&A)
  - Login with User ID or Email
  - Password requirements: 8+ characters, 1 uppercase, 1 number
  
- **Home Screen**
  - Navigation to all three search methods
  - Clean Material Design 3 UI
  
- **Text Search**
  - Product search interface
  - Product listing with vendor and price information
  - Ready for backend API integration
  
- **Image Search**
  - Upload image or take photo
  - UI ready for Computer Vision integration
  
- **AI Assistant** ✨ **NEW: Now with Persistent Chat History!**
  - Chat interface with message history
  - **SQLite database for persistent storage**
  - **Messages survive app restarts**
  - **Clear history with confirmation**
  - **Timestamps on all messages**
  - **Real-time reactive updates**
  - Ready for Gemini API integration

### 🚧 To Be Implemented (Backend Integration)
- Web scraping with BeautifulSoup (Python backend)
- Computer Vision for image recognition
- Gemini API for AI Assistant responses
- SQLite database for user data and product search history

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material Design 3
- **Database**: Room (SQLite) for chat history persistence
- **Architecture**: MVVM with Repository pattern
- **Async**: Kotlin Coroutines and Flow
- **Backend** (planned): Python with BeautifulSoup for web scraping
- **AI/ML** (planned): Gemini API for conversational AI
- **Minimum Android Version**: API 24 (Android 7.0 Nougat)
- **Target Android Version**: API 34

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/democse3310/
│   │   ├── MainActivity.kt              # Main navigation host
│   │   ├── LoginScreen.kt               # User login
│   │   ├── RegistrationScreen.kt        # User registration
│   │   ├── HomeScreen.kt                # Main dashboard
│   │   ├── TextSearchScreen.kt          # Text-based product search
│   │   ├── ImageSearchScreen.kt         # Reverse image search
│   │   ├── AiAssistantScreen.kt         # AI chat interface
│   │   ├── data/
│   │   │   ├── ChatMessageEntity.kt     # Chat message table schema
│   │   │   ├── ChatMessageDao.kt        # Database operations
│   │   │   ├── ChatDatabase.kt          # Room database
│   │   │   ├── Product.kt               # Product data model
│   │   │   └── User.kt                  # User data model
│   │   ├── repository/
│   │   │   ├── ChatRepository.kt        # Chat data abstraction
│   │   │   └── GenerativeAiRepository.kt # AI integration
│   │   └── viewmodel/
│   │       ├── AiAssistantViewModel.kt  # Chat state management
│   │       ├── AiAssistantViewModelFactory.kt # DI factory
│   │       └── TextSearchViewModel.kt   # Search logic
│   ├── res/                             # Resources (layouts, strings, etc.)
│   └── AndroidManifest.xml
└── build.gradle.kts                     # App dependencies

Documentation/
├── IMPLEMENTATION_SUMMARY.md            # Complete implementation overview
├── SQLITE_IMPLEMENTATION.md             # Detailed database guide
├── ARCHITECTURE_DIAGRAM.md              # Visual architecture explanation
├── MIGRATION_GUIDE.md                   # Apply patterns to other features
├── CODE_REFERENCE.md                    # Quick code snippets
├── TESTING_GUIDE.md                     # Testing strategies
└── TROUBLESHOOTING.md                   # Common issues and solutions
```

## New: SQLite Chat History Implementation 🎉

The AI Assistant now features a complete, production-ready SQLite database implementation for persistent chat history. This means:

✅ **All conversations are saved** - Messages persist across app sessions
✅ **Real-time updates** - UI automatically reflects database changes
✅ **Timestamps** - Every message has a timestamp
✅ **Session management** - Conversations can be grouped
✅ **Clear history** - Users can delete all messages with confirmation
✅ **Clean architecture** - MVVM with Repository pattern
✅ **Fully documented** - 6 comprehensive guide documents included

### Quick Start with Chat History

1. Run the app
2. Navigate to AI Assistant
3. Send some messages
4. Close and reopen the app
5. Your messages are still there! 🎊

### Documentation

See these detailed guides:

- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Start here for overview
- **[SQLITE_IMPLEMENTATION.md](SQLITE_IMPLEMENTATION.md)** - How it all works
- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - If something goes wrong
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - How to test the implementation
- **[MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)** - Apply to other features
- **[CODE_REFERENCE.md](CODE_REFERENCE.md)** - Quick code lookup

## Setup & Installation

### Prerequisites
- Android Studio (latest version)
- JDK 17 or higher
- Android SDK API 34
- Gradle 8.13+

### Steps

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd "CSE 3310"
   ```

2. **Open in Android Studio**
   - File → Open → Select the `CSE 3310` folder
   - Wait for Gradle sync to complete
   - Sync will download Room and KSP dependencies

3. **Configure Gradle JDK**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Set Gradle JDK to JDK 17 or higher (Android Studio's embedded JDK works)

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the green play button ▶️
   - Or press Shift+F10

5. **Test Chat History**
   - Navigate to AI Assistant
   - Send a few messages
   - Close app completely
   - Reopen and verify messages persist

## Building

### Debug Build
```bash
./gradlew assembleDebug
```
APK will be in `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (for production)
```bash
./gradlew assembleRelease
```

## Database

The app uses Room (SQLite) for local data persistence:

**Chat History Database:**
- **Table**: `chat_messages`
- **Location**: `/data/data/com.example.democse3310/databases/chat_database`
- **Fields**: id, text, isUser, timestamp, sessionId
- **Version**: 1

To view the database:
1. Run app in debug mode
2. View → Tool Windows → App Inspection
3. Select Database Inspector
4. Explore `chat_messages` table

## Dependencies

Key libraries used:

```kotlin
// UI & Compose
androidx.compose.material3
androidx.navigation:navigation-compose

// Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Coroutines
kotlinx-coroutines-android:1.7.3

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
```

## API Integration (Future)

When implementing the Python backend API, you'll need to:

1. Create an `apikeys.properties` file in the app root (already in .gitignore):
   ```properties
   GEMINI_API_KEY=your_key_here
   BACKEND_API_URL=your_backend_url
   ```

2. Load keys in `build.gradle.kts`:
   ```kotlin
   android {
       defaultConfig {
           buildConfigField("String", "GEMINI_API_KEY", "\"${project.property("GEMINI_API_KEY")}\"")
       }
   }
   ```

3. Update `generateAiResponse()` in `AiAssistantViewModel.kt` to call Gemini API

## Team Members

- Adib Bin Kadir
- Karim Gomaa - *Lead Developer, SQLite Implementation*
- Fauzaan Mohammed
- Basil Awad Yousif

## Project Timeline

- **Increment 1**: SRA Document ✅ (Due: 10/30/2025)
- **Increment 2**: UI Implementation ✅ (Due: 11/20/2025)
- **Increment 2.5**: SQLite Chat History ✅ (Completed: 12/01/2025)
- **Increment 3**: Backend Integration 🚧 (Due: 11/27/2025)
- **Increment 4**: Final Delivery & Presentation 📅 (Due: 12/2/2025)

## Recent Updates

### Version 1.1 - SQLite Integration (12/01/2025)
- ✅ Added Room database for chat history
- ✅ Implemented MVVM architecture with Repository pattern
- ✅ Real-time reactive updates with Kotlin Flow
- ✅ Message persistence across app restarts
- ✅ Clear history feature with confirmation
- ✅ Timestamps on all messages
- ✅ Session management support
- ✅ Comprehensive documentation (6 guides)
- ✅ Loading indicators
- ✅ Error handling
- ✅ Auto-scroll to newest messages

## Contributing

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused

### Testing
- Test all new features manually
- Verify database persistence
- Check error handling
- Test on multiple Android versions

### Documentation
- Update README for major features
- Add inline comments
- Document API endpoints when added

## Troubleshooting

### Common Issues

**Gradle sync fails:**
- Ensure JDK 17+ is selected
- Check internet connection
- Invalidate caches: File → Invalidate Caches → Restart

**App crashes on chat screen:**
- Verify Room dependencies are synced
- Check ViewModelFactory is used correctly
- See TROUBLESHOOTING.md for detailed help

**Messages not persisting:**
- Verify database is created (use Database Inspector)
- Check coroutine usage in ViewModel
- See SQLITE_IMPLEMENTATION.md for debugging tips

### Getting Help

1. Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
2. Use Database Inspector to verify data
3. Check Logcat for error messages
4. Review [TESTING_GUIDE.md](TESTING_GUIDE.md)

## License

Academic project for CSE 3310, Fall 2025

## Acknowledgments

- Android Jetpack team for Room and Compose
- Material Design 3 guidelines
- Kotlin Coroutines documentation
- CSE 3310 course staff

## Notes

- The HelloWorld folder in the parent directory is NOT part of this project and is excluded from version control
- No API keys are currently in the codebase
- All backend integrations are marked with TODO comments in the code
- Database uses `.fallbackToDestructiveMigration()` for development (will need proper migrations for production)
- Chat history implementation follows Android best practices and is production-ready
- Full source code and documentation available in repository

---

**Latest Update**: SQLite chat history implementation complete with full documentation. See `IMPLEMENTATION_SUMMARY.md` for details.
