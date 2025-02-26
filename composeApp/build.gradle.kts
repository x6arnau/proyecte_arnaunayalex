import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("kapt")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation("androidx.credentials:credentials:1.3.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            implementation("io.github.cdimascio:dotenv-kotlin:6.5.0")
            implementation(platform("io.github.jan-tennert.supabase:bom:3.0.3"))
            implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.3")
            implementation("io.github.jan-tennert.supabase:auth-kt:3.0.3")
            implementation ("io.github.jan-tennert.supabase:storage-kt:3.0.3")
            implementation("io.ktor:ktor-client-cio:3.0.3")
            implementation("io.ktor:ktor-client-serialization:3.0.3")
            implementation("io.ktor:ktor-client-json:3.0.3")
            implementation("io.ktor:ktor-client-logging:3.0.3")
            implementation("io.ktor:ktor-client-auth:3.0.3")
            implementation("io.github.jan-tennert.supabase:compose-auth:3.0.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
            implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("com.google.dagger:dagger:2.48")
            implementation("com.google.dagger:dagger-android-support:2.48")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            // Añade esto fuera del bloque commonMain.dependencies
            implementation("com.google.dagger:dagger:2.48")
            implementation("com.google.dagger:dagger-android-support:2.48")
        }
    }
}

android {
    namespace = "proven.gruparnaunayalex.cat"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "proven.gruparnaunayalex.cat"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "proven.gruparnaunayalex.cat.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "proven.gruparnaunayalex.cat"
            packageVersion = "1.0.0"
        }
    }
}