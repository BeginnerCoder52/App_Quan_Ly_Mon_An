plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Lưu ý: Plugin này dành cho Kotlin 2.0+. Nếu bạn dùng Kotlin cũ hơn và bị lỗi dòng này, hãy xóa nó đi.
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.app_quan_ly_do_an"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app_quan_ly_do_an"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // QUAN TRỌNG: Nâng lên Java 17 để tương thích với Android Studio mới và compileSdk 35
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (Bill of Materials) - Giúp quản lý phiên bản các thư viện UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- CÁC THƯ VIỆN BỔ SUNG (Đã cập nhật phiên bản mới ổn định) ---

    // Navigation Compose (Phiên bản 2.8.0 ổn định cho SDK 34/35)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Icons Extended (Bộ icon đầy đủ)
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}