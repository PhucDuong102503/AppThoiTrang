plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fashionshopapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fashionshopapp"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {


    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //RxJava cho phép xử lý các luồng dữ liệu từ nhiều nguồn khác nhau như sự kiện người dùng (click, vuốt), các yêu cầu API, hoặc các biến số.
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    // Retrofit để đơn giản hóa việc giao tiếp với các dịch vụ web RESTful, de thuc hien GET PUT....
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //glider
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
}