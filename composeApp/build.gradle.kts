import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("app.cash.sqldelight") version "2.0.2"
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
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation ("io.github.jan-tennert.supabase:postgrest-kt:3.0.3")
            implementation ("io.ktor:ktor-client-android:3.0.3")
        }
        commonMain.dependencies {
            implementation("io.coil-kt.coil3:coil-compose:3.1.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
            implementation("androidx.credentials:credentials:1.3.0")
            implementation(compose.material3)
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
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
            implementation("org.slf4j:slf4j-nop:1.7.36")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            // Añade esto fuera del bloque commonMain.dependencies
            implementation("com.google.dagger:dagger:2.48")
            implementation("com.google.dagger:dagger-android-support:2.48")
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }
    }
}

android {
    namespace = "proven.gruparnaunayalex.cat"
    compileSdk = 35
    // libs.versions.android.compileSdk.get().toInt()

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
    implementation(libs.postgrest.kt.android.debug)
    implementation(libs.postgrest.kt.android.debug)
    implementation(libs.androidx.room.common)
    implementation(libs.firebase.database.ktx)
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
sqldelight {
    databases {
        create("Database") {
            packageName.set("proven.gruparnaunayalex.cat")
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")
        }
    }
}