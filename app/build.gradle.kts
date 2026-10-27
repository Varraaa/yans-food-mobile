import java.io.File
import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
}

// Otomatis mengekstrak logo_yans_food.png dari Icon Yans Food.svg lalu memindahkan SVG ke root
val drawableFolder = File(projectDir, "src/main/res/drawable")
val rawSvg = File(drawableFolder, "Icon Yans Food.svg")
if (rawSvg.exists()) {
    try {
        val svgContent = rawSvg.readText()
        val base64Prefix = "data:image/png;base64,"
        val startIndex = svgContent.indexOf(base64Prefix)
        if (startIndex != -1) {
            val base64Data = svgContent.substring(startIndex + base64Prefix.length).substringBefore("\"")
            val imageBytes = Base64.getDecoder().decode(base64Data)
            val pngFile = File(drawableFolder, "logo_yans_food.png")
            pngFile.writeBytes(imageBytes)
            val backupFile = File(rootDir, "Icon_Yans_Food_original.svg")
            rawSvg.copyTo(backupFile, overwrite = true)
            rawSvg.delete()
        }
    } catch (_: Exception) {
    }
}

android {
    namespace = "com.example.yansfood"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.yansfood"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}