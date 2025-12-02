plugins {
    id("com.android.application") version "8.13.1"
    id("org.jetbrains.kotlin.android") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.example.democse3310"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.democse3310"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Load API keys from local.properties (if present). We support both
        // `GEMINI_API_KEY` and `geminiApiKey` property names for convenience.
        val properties = java.util.Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        // Resolve the gemini key: prefer explicit GEMINI_API_KEY, then camelCase,
        // then fall back to environment variable if present.
        val geminiKey: String = properties.getProperty("GEMINI_API_KEY", properties.getProperty("geminiApiKey", System.getenv("GEMINI_API_KEY") ?: ""))
        // Backend URL for local/dev testing. Set BACKEND_API_URL in local.properties
        val backendUrl: String = properties.getProperty("BACKEND_API_URL", "http://127.0.0.1:8000")
    }

    buildTypes {
        debug {
            // Expose the GEMINI key and backend URL only in debug builds for local development.
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
            buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
        }

        release {
            // Do not embed the key in release builds.
            buildConfigField("String", "GEMINI_API_KEY", "\"\"")
            buildConfigField("String", "BACKEND_URL", "\"\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // ViewModel and State Management
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    
    // JSON Parsing
    implementation("org.json:json:20231013")
    
    // Networking for Apify
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}