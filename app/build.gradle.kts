plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.eventhub_jigsaw"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventhub_jigsaw"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

}


dependencies {
    //Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    // Android testing
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    // Additional libraries
    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
}