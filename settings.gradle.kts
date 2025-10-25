// File: settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ⭐ THÊM DÒNG NÀY VÀO ĐÂY ⭐
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FashionShopApp"
include(":app")
