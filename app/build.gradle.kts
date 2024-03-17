plugins {
    id("com.android.application")
}

android {
    namespace = "com.ytt.informationrelease"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ytt.informationrelease"
        minSdk = 21
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.yanzhenjie.andserver:api:2.1.12")
    implementation("com.yanzhenjie.andserver:processor:2.1.12")
    implementation("com.tencent.tbs:tbssdk:44286")
    implementation("com.blankj:utilcode:1.30.7")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("com.airbnb.android:lottie:6.3.0")
}