plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.10"
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.bundles.android.build)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.dokka.plugin)
    implementation(libs.androidx.navigation.safeargs.plugin)
    implementation(libs.hilt.plugin)
}

gradlePlugin {
    plugins {
        create("frybitsAppPlugin") {
            id = "frybits-application"
            implementationClass = "com.frybits.gradle.FrybitsApplicationPlugin"
        }

        create("frybitsLibraryPlugin") {
            id = "frybits-library"
            implementationClass = "com.frybits.gradle.FrybitsLibraryPlugin"
        }

        create("frybitsTestPlugin") {
            id = "frybits-test"
            implementationClass = "com.frybits.gradle.FrybitsTestPlugin"
        }
    }
}

