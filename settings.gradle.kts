pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("agp", "8.2.2")
            version("kotlin", "1.9.22")
            
            plugin("android.application", "com.android.application").versionRef("agp")
            plugin("kotlin.android", "org.jetbrains.kotlin.android").versionRef("kotlin")
            
            library("core.ktx", "androidx.core:core-ktx:1.12.0")
        }
    }
}

rootProject.name = "Trimly"
include(":app")
 