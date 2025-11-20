# ShopperAI - AI-Powered Shopping Assistant

**Team 12 - CSE 3310, Fall 2025**

## Project Overview

ShopperAI is an Android application that helps users find products and compare prices using AI-powered search capabilities. The app provides three main search methods:

1. **Text-Based Search** - Search for products by name or description
2. **Reverse Image Search** - Upload or capture images to find similar products
3. **AI Assistant Chat** - Conversational interface for shopping queries

## Features

### ✅ Implemented (Skeleton/UI)
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
  
- **AI Assistant**
  - Chat interface with message history
  - Ready for Gemini API integration

### 🚧 To Be Implemented (Backend Integration)
- Web scraping with BeautifulSoup (Python backend)
- Computer Vision for image recognition
- Gemini API for AI Assistant
- SQLite database for user data and search history
- Price comparison across multiple vendors

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material Design 3
- **Backend** (planned): Python with BeautifulSoup for web scraping
- **AI/ML** (planned): Gemini API for conversational AI
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

### Steps

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd "CSE 3310"
   ```

2. **Open in Android Studio**
   - File → Open → Select the `CSE 3310` folder
   - Wait for Gradle sync to complete

3. **Configure Gradle JDK**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Set Gradle JDK to JDK 17 or higher (Android Studio's embedded JDK works)

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
