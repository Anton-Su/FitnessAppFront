import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka") version "1.8.20"
}

android {
    namespace = "com.example.fitnessapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fitnessapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.compose.foundation.layout)
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.10.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.10.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.9.1")
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


tasks.named("dokkaHtml", org.jetbrains.dokka.gradle.DokkaTask::class) {
    outputDirectory.set(layout.buildDirectory.dir("dokka/html").get().asFile)
    dokkaSourceSets {
        named("main") {
            moduleName.set("FitnessApp")
            jdkVersion.set(11)
            sourceRoots.from(file("src/main/java"), file("src/main/kotlin"))
            perPackageOption {
                matchingRegex.set("com.example.*")
                includeNonPublic.set(false)
            }
        }
    }
}

tasks.named("dokkaJavadoc", org.jetbrains.dokka.gradle.DokkaTask::class) {
    outputDirectory.set(layout.buildDirectory.dir("dokka/javadoc").get().asFile)
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(layout.buildDirectory.dir("dokka/javadoc").get().asFile)
}
