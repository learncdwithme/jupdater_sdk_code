/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.library")
    id("maven-publish")
}

// 1. Create an empty properties object
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

// 2. Load the file if it exists
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

// Clean raw strings loaded from local.properties if they include escaped quotes
fun String.unquote(): String = this.removeSurrounding("\"")
var sdkName  = localProperties.getProperty("SDK_NAME", "JUpdater").unquote()
var sdkVersion = localProperties.getProperty("VERSION", "1.0.0").unquote()
val buildNumber = localProperties.getProperty("BUILDNUMBER", "1").unquote()

// Set root Maven coordinates for JitPack
group = "com.github.vijayjvvc"
version = sdkVersion

android {
    namespace = "com.qtlws.android.jupdater"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        consumerProguardFiles("proguard-rules.pro")

        // Pass version info to BuildConfig
        buildConfigField("String", "SDK_NAME", "\"${sdkName}\"")
        buildConfigField("String", "VERSION", "\"${sdkVersion}\"")
        buildConfigField("String", "BUILDNUMBER", "\"${buildNumber}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        buildConfig = true
    }

    publishing {
        singleVariant("release"){
            withSourcesJar()
        }
    }

//    // Optional: To generate consistent filenames
//    libraryVariants.all {
//        outputs.all {
//            val output = this as com.android.build.gradle.internal.api.LibraryVariantOutputImpl
//            val variantName = output.baseName.replaceFirstChar {
//                if (it.isLowerCase()) it.titlecase(
//                    Locale.getDefault()
//                ) else it.toString()
//            }
////            val buildType = output.b.name.capitalize()
//            output.outputFileName = "jupdater-V${version}-${variantName.toLowerCase()}.aar"
//        }
//    }


}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}


// Expose the release AAR component to Maven publication
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                artifactId = "jupdater_sdk_code"
            }
        }
    }
}

tasks.register("buildDebugAar") {
    dependsOn(":app:assembleDebug") // Task to build the debug variant
    description = "Builds the debug AAR for the jupdater library."
    group = "build"
}

tasks.register("buildReleaseAar") {
    dependsOn(":app:assembleRelease")
    description = "Builds the release AAR for the jupdater library."
    group = "build" // This will group it under the 'build' category in the Gradle window
}
