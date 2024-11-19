plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "readyfiji.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "readyfiji.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 15
        versionName = "2.5"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform(libs.firebase.bom))

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.mysql.connector.java)
    implementation(libs.recyclerview)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.drawerlayout)
    implementation("com.amazonaws:aws-android-sdk-s3:2.22.+")
    implementation (libs.glide)
    implementation (libs.circleimageview)
    implementation (libs.material.v180)
    annotationProcessor (libs.compiler)
    implementation (libs.logging.interceptor)



}