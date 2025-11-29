# Setting Up Gemini API

## Get Your API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key

## Add to Your Project

1. Open (or create) `local.properties` in the app root
2. Add this line:
   ```
   GEMINI_API_KEY=paste_your_key_here
   ```

3. Open `app/src/main/java/com/example/democse3310/viewmodel/AiAssistantViewModel.kt`
4. Replace this line:
   ```kotlin
   private val apiKey = "YOUR_GEMINI_API_KEY_HERE"
   ```
   With:
   ```kotlin
   private val apiKey = System.getenv("GEMINI_API_KEY") ?: "fallback_key"
   ```

## For Production (BuildConfig approach)

Add to `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        
        buildConfigField("String", "GEMINI_API_KEY", 
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

Then use in code:
```kotlin
private val apiKey = BuildConfig.GEMINI_API_KEY
```

**Note:** `local.properties` is already in `.gitignore` so your key won't be committed to Git!
