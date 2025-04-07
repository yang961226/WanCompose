import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
    id("com.google.protobuf")
    id("de.jensklingenberg.ktorfit") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File,
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

android {
    namespace = "com.sundayting.wancompose"
    compileSdk = 35

    tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.FlowPreview"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=dev.chrisbanes.snapper.ExperimentalSnapperApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.ui.text.ExperimentalTextApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }

    defaultConfig {
        applicationId = "com.sundayting.wancompose"
        minSdk = 26
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        aidl = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets {
        getByName("main") {
            proto {
                srcDir("src/main/protocolbuffers")
            }
        }
    }
}

protobuf {
    protoc {
        artifact = if (project.hasProperty("protoc_platform")) {
            "com.google.protobuf:protoc:3.0.0:${project.property("protoc_platform")}"
        } else {
            "com.google.protobuf:protoc:3.0.0"
        }
    }
    plugins {
        id("javalite") {
            artifact = if (project.hasProperty("protoc_platform")) {
                "com.google.protobuf:protoc-gen-javalite:3.0.0:${project.property("protoc_platform")}"
            } else {
                "com.google.protobuf:protoc-gen-javalite:3.0.0"
            }
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("javalite") {}
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    val composeBomVersion = "2025.02.00"

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.navigation:navigation-compose:2.8.8")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.webkit:webkit:1.12.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    implementation("com.google.zxing:core:3.3.3")

    val ktorfitVersion = "2.4.0"
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")

    val ktorVersion = "3.1.0"
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")


    // Proto DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.3")
    implementation("com.google.protobuf:protobuf-lite:3.0.0")


    val hiltVersion = "2.55"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    val roomVersion = "2.7.0-rc01"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$roomVersion")

    val lifecycleVersion = "2.8.7"
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    val cameraxVersion = "1.4.1"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    implementation("androidx.camera:camera-mlkit-vision:${cameraxVersion}")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")

    val appcompatVersion = "1.6.1"
    implementation("androidx.appcompat:appcompat:$appcompatVersion")

    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("org.jsoup:jsoup:1.18.3")     // html解析

}