plugins {
    id("com.android.application")
}

android {
    namespace = "br.com.metaro.portal"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.metaro.portal"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "1.5.0"
    }

    flavorDimensions += "ambiente"
    productFlavors {
        create("producao") {
            dimension = "ambiente"
        }
        create("homologacao") {
            dimension = "ambiente"
            applicationIdSuffix = ".teste"
            versionNameSuffix = "-teste"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(files("libs/kotlin-stdlib-2.2.10.jar"))
}
