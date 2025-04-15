import java.net.URI
import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.openapi.generator)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.grupo1.deremate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grupo1.deremate"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildFeatures {
        viewBinding = true
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
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.scalars)
    implementation(libs.threetenabp)
    implementation(libs.kotlinx.datetime)

    // JJWT Library for JWT parsing
    implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly ("io.jsonwebtoken:jjwt-gson:0.11.5")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    // Lifecycle runtime (puede ser necesaria, especialmente con Java 8+)
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0") // O última estable

    // Opcional pero recomendado para habilitar Java 8 con Lifecycle
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0") // O última estable

    implementation("com.google.android.material:material:1.12.0") // Use latest material version
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Use latest retrofit version
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Or your preferred converter

    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("androidx.biometric:biometric:1.2.0-alpha05")
}

kapt {
    correctErrorTypes = true
}



val openApiOutputDir = file("$projectDir/src/main/java")

val downloadOpenApiSpec by tasks.registering {
    val outputFile = layout.buildDirectory.file("tmp/openapi/api.json")

    outputs.file(outputFile)

    doLast {
        val uri = URI("http://localhost:8080/v3/api-docs")
        val url = uri.toURL()
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()

        println("Descargando OpenAPI spec desde $url...")

        url.openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        println("Guardado en: ${file.absolutePath}")
    }
}


tasks.named("openApiGenerate").configure {
    dependsOn(downloadOpenApiSpec)
    (this as org.openapitools.generator.gradle.plugin.tasks.GenerateTask).apply {
        generatorName.set("kotlin")
        inputSpec.set(layout.buildDirectory.file("tmp/openapi/api.json").get().asFile.toURI().toString())
        configOptions.set(
            mapOf(
                "library" to "jvm-retrofit2",
                "serializationLibrary" to "gson",
                "packageName" to "com.grupo1.deremate",
                "apiPackage" to "com.grupo1.deremate.apis",
                "modelPackage" to "com.grupo1.deremate.models",
                "invokerPackage" to "com.grupo1.deremate.infrastructure"
            )
        )
    }
}

val generateAPI by tasks.registering(Copy::class) {
    dependsOn("openApiGenerate")

    val buildDir = "build/generate-resources/main/src/main/kotlin/com/grupo1/deremate/"
    val outputDir = "src/main/java/com/grupo1/deremate/"

    into(outputDir)

    from(buildDir + "apis") {
        into("apis")
    }

    from(buildDir + "models") {
        into("models")
    }

    from(buildDir + "infrastructure") {
        into("infrastructure")
        filter { line ->
            line
                .replace(
                    ".registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())",
                    ""
                ) // o lo que prefieras
                .replace(
                    ".registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())",
                    ""
                ) // o lo que prefieras
                .replace(
                    ".registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())",
                    ""
                ) // o lo que prefieras
        }
        exclude("**/*DateTimeAdapter.kt")
        exclude("**/*LocalDateAdapter.kt")
    }

    doFirst {
        println("Copiando OpenAPI generado a src/main/java/com/grupo1/deremate")
    }
}

