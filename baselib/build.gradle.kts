plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.benwu.baselib"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.recyclerview)
    api(libs.androidx.viewpager2)
    api(libs.androidx.swiperefreshlayout)

    // lifecycle
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)

    // dataStore
    api(libs.androidx.datastore.preferences)

    // paging
    api(libs.androidx.paging.runtime.ktx)

    // retrofit
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)

    // okhttp
    api(platform(libs.okhttp.bom))
    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)

    // gson
    api(libs.gson)

    // glide
    api(libs.glide)

    // lottie
    api(libs.lottie)

    // navigation
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)

    // autoSize
    api(libs.androidAutoSize)
}