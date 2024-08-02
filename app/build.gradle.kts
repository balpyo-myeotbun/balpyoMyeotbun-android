import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.googleGmsGoogleServices)
}

android {
    namespace = "com.project.balpyo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.balpyo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","server_url",getApiKey("server_url"))
        buildConfigField("String","develop_url",getApiKey("develop_url"))
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dataBinding {
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

fun getApiKey(propertyKey:String) : String {
    return  gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // seekbar library
    implementation("com.github.warkiz.widget:indicatorseekbar:2.1.2")
    implementation("com.google.android.exoplayer:exoplayer:2.15.1")
    // recyclerView library
    implementation("com.arasthel:spannedgridlayoutmanager:3.0.2")

    // api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 변환
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // OkHttp 라이브러리
    implementation("com.squareup.okhttp3:logging-interceptor:3.11.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //문장 분리기
    implementation ("kr.bydelta:koalanlp-okt:2.1.4")

    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    // LiveData (선택적, 데이터 변화를 관찰할 경우 사용)
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    //네비게이션
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.3")
    //바텀네비게이션
    implementation ("com.google.android.material:material:1.4.0")

    //FCM
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    //뷰페이저
    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    // gif
    implementation("com.airbnb.android:lottie:5.0.2")

    // tooltip
    implementation("com.github.skydoves:balloon:1.6.6")
}