import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vungn.application"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vungn.application"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val date = Date()
        val formattedDate = SimpleDateFormat("ddMMyyHHmm").format(date)
        val appName = applicationId!!.split('.').last()
        val archivesBaseName =
            "${appName}-${versionCode}-${versionName}-${formattedDate}"
        setProperty("archivesBaseName", archivesBaseName)
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "PRIVACY_POLICY_URL",
                "\"\""
            )
            buildConfigField(
                "String",
                "API_URL",
                "\"\""
            )
            buildConfigField("String", "API_TOKEN", "\"token WlmuphKctFUjV6BwizYlSXzbPGUWqsFf\"")
            buildConfigField(
                "String",
                "DATABASE_NAME",
                "\"${android.defaultConfig.applicationId!!.replace('.', '_')}_db\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "PRIVACY_POLICY_URL",
                "\"\""
            )
            buildConfigField(
                "String",
                "API_URL",
                "\"\""
            )
            buildConfigField("String", "API_TOKEN", "\"\"")
            buildConfigField(
                "String",
                "DATABASE_NAME",
                "\"${android.defaultConfig.applicationId!!.replace('.', '_')}_db\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":showcaseviewlib"))
    implementation(project(":prdownloader"))
    implementation(project(":blur"))

    // Firebase
    implementation(libs.firebase.bom)
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // Ads
    implementation(libs.play.services.ads)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.guava)
    implementation(libs.listenablefuture)

    // Icepick
    implementation(libs.icepick)
    annotationProcessor(libs.icepick.processor)

    // Worker
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)

    // Little animation
    implementation(libs.lottie)

    // YoYo
//    implementation("com.daimajia.androidanimations:library:2.4@aar")

    // Webkit
    implementation(libs.androidx.webkit)

    // Language
    implementation(libs.lingver)

    // Dots indicator
    implementation(libs.dotsindicator)

    // Data store
    implementation(libs.androidx.datastore.preferences)

    // Color picker
//    implementation(libs.colorpickerview)

    // Camera X
//    implementation(libs.camera.core)
//    implementation(libs.camera2)
//    implementation(libs.camera.view)
//    implementation(libs.camera.video)
//    implementation(libs.camera.lifecycle)

    // Glide
    implementation(libs.glide)

    // Gson
    implementation(libs.gson)

    // Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    // Nav
    implementation(libs.navFragment)
    implementation(libs.navUi)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}