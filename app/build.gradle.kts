plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
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
    // ================== FIREBASE ==================
    // 1. Thêm Firebase BoM (Bill of Materials) để quản lý phiên bản
    // Bạn chỉ cần khai báo BoM, nó sẽ tự chọn phiên bản tương thích cho các thư viện Firebase khác.
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // 2. Thư viện Cloud Firestore để chat (bạn đã có qua libs)
    implementation(libs.firebase.firestore)

    // Thêm thư viện Firebase Messaging (quan trọng cho notification)
    implementation("com.google.firebase:firebase-messaging")
    // ==============================================


    // --- Các thư viện khác của bạn (giữ nguyên) ---
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
    //RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //glider
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")

    // Khác
    implementation("com.nex3z:notification-badge:1.0.4")
    implementation("org.greenrobot:eventbus:3.2.0")
    implementation("io.github.pilgr:paperdb:2.7.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
