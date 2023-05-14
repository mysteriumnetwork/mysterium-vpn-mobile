plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "network.mysterium.provider"
    compileSdk = 33

    defaultConfig {
        applicationId = "network.mysterium.provider"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    implementation(files("libs/provider-mobile-node.aar"))

    api(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist.tools)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.di)
    implementation(libs.markdown)

    debugImplementation(libs.bundles.compose.debug)
}
