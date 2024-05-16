plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tbmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tbmanager"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures{
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-instantapps:18.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.jjoe64:graphview:4.2.2")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation ("org.java-websocket:Java-WebSocket:1.5.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("io.reactivex.rxjava2:rxjava:2.0.8")
    implementation ("io.reactivex.rxjava2:rxandroid:2.0.1")

    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.android.gms:play-services-vision:20.1.3")
    implementation ("com.google.zxing:core:3.4.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.android.support:cardview-v7:28.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}