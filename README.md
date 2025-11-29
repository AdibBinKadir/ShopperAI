# ShopperAI - AI-Powered Shopping Assistant

**Team 12 - CSE 3310, Fall 2025**

## Project Overview

ShopperAI is an Android application that helps users find products and compare prices using AI-powered search capabilities. The app provides three main search methods:

1. **Text-Based Search** - Search for products by name or description
2. **Reverse Image Search** - Upload or capture images to find similar products
3. **AI Assistant Chat** - Conversational interface for shopping queries

## Features

### ✅ Implemented
- **User Authentication**
  - Registration with validation (name, email, phone, user ID, password)
  - Login with User ID or Email
  - Password requirements: 8+ characters, 1 uppercase, 1 number
  
- **Home Screen**
  - Navigation to all three search methods
  - Clean Material Design 3 UI
  
- **Text Search**
  - Product search interface
  - Product listing with vendor and price information
  - Ready for shopping API integration
  
- **Image Search**
  - Upload image or take photo UI
  - Ready for Computer Vision integration
  
- **AI Assistant**
  - ✨ **Integrated with Gemini AI** - Real conversational shopping assistant
  - Chat interface with full message history
  - Powered by Google's Gemini Pro model

### 🚧 To Be Implemented
- Direct integration with shopping APIs (Amazon Product API, eBay API, etc.)
- Computer Vision for reverse image search
- SQLite database for user data and search history
- Price comparison across multiple vendors

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material Design 3
- **AI**: Google Gemini Pro API for conversational AI
- **Networking**: Retrofit 2 + OkHttp for API calls
- **Architecture**: MVVM with ViewModels and StateFlow
- **Database** (planned): SQLite
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
│   │   │   ├── Product.kt               # Product data model
│   │   │   └── User.kt                  # User data model
│   │   └── viewmodel/
│   │       └── TextSearchViewModel.kt   # Search logic
│   ├── res/                             # Resources (layouts, strings, etc.)
│   └── AndroidManifest.xml
└── build.gradle.kts                     # App dependencies
```

## Setup & Installation

### Prerequisites
- Android Studio (latest version)
- JDK 17 or higher
- Android SDK API 34
- Gradle 8.13+
- **Gemini API Key** (free from [Google AI Studio](https://makersuite.google.com/app/apikey))

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/AdibBinKadir/ShopperAI.git
   cd ShopperAI
   ```

2. **Open in Android Studio**
   - File → Open → Select the `app` folder
   - Wait for Gradle sync to complete

3. **Configure Gradle JDK**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Set Gradle JDK to JDK 17 or higher (Android Studio's embedded JDK works)

4. **Add Gemini API Key**
   - See [GEMINI_SETUP.md](GEMINI_SETUP.md) for detailed instructions
   - Quick: Add `GEMINI_API_KEY=your_key` to `local.properties`
   - Update the API key in `AiAssistantViewModel.kt`

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the green play button ▶️
   - Or press Shift+F10

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

## Team Members

- Adib Bin Kadir
- Karim Gomaa
- Fauzaan Mohammed
- Basil Awad Yousif

## Project Timeline

- **Increment 1**: SRA Document ✅ (Due: 10/30/2025)
- **Increment 2**: UI Implementation ✅ (Due: 11/20/2025)
- **Increment 3**: Backend Integration 🚧 (Due: 11/27/2025)
- **Increment 4**: Final Delivery & Presentation 📅 (Due: 12/2/2025)

## License

Academic project for CSE 3310, Fall 2025

## Notes

- The HelloWorld folder in the parent directory is NOT part of this project and is excluded from version control
- No API keys are currently in the codebase
- All backend integrations are marked with TODO comments in the code
