# Testing Steps to Find the Crash

Since we can't see the crash logs, let's narrow down exactly where it crashes:

## Test 1: Does BuildConfig have the API key?
Add this to HomeScreen to test if BuildConfig loads:
```kotlin
Text("API Key: ${BuildConfig.GEMINI_API_KEY.take(10)}...")
```
If this crashes → BuildConfig issue

## Test 2: Can we create GenerativeModel?
In AiAssistantViewModel, add a test button that only tries to create the model:
```kotlin
fun testInit() {
    viewModelScope.launch {
        try {
            val model = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "test-key"
            )
            _messages.value = _messages.value + ChatMessage("✓ Model created!", false)
        } catch (e: Throwable) {
            _messages.value = _messages.value + ChatMessage("✗ Failed: ${e.message}", false)
        }
    }
}
```

## Test 3: Is it the actual API call?
After model creation works, try a simple API call

## Most Likely Issues:
1. **Proguard/R8 is obfuscating Gemini SDK** - Add keep rules
2. **Missing internet permission at runtime** - Need to check permission
3. **Dependency conflict we can't see** - Need to exclude more libs
4. **BuildConfig not being generated for release builds** - Check build variant

## Next Step:
Tell me which test fails and I'll fix that specific issue.
