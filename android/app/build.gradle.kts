import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.sentry)
    alias(libs.plugins.serialization)

}

android {
    namespace = "network.mysterium.provider"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "network.mysterium.provider"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 4
        versionName = "3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["SENTRY"] = gradleLocalProperties(rootDir).getProperty("sentry")
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
        sourceCompatibility(1.8)
        targetCompatibility(1.8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    packagingOptions {
        resources {
            resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(":node"))

    api(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist.tools)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.di)
    implementation(libs.markdown)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.serialization)

    debugImplementation(libs.bundles.compose.debug)
}
