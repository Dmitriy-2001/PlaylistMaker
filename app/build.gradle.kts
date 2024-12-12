plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // Поддержка Parcelable для Kotlin
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 34
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Основные зависимости Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Тестирование
    testImplementation("junit:junit:4.13.2") // JUnit для модульного тестирования
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // Расширения JUnit для Android
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Espresso для UI тестов
    implementation("androidx.test.ext:junit-ktx:1.2.1") // Kotlin extensions для JUnit
    implementation("androidx.test:monitor:1.7.1") // Мониторинг приложений при тестировании

    // Glide для загрузки изображений
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    // Gson для работы с JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit для сетевых запросов
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Koin для внедрения зависимостей
    implementation("io.insert-koin:koin-android:3.3.0")

    // Фрагменты
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    // ViewPager2 для работы с перелистыванием экранов
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Navigation Component для навигации
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Keyboard Visibility Event для отслеживания видимости клавиатуры
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC3")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    // Android Jetpack Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
}
