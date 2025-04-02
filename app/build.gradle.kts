import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.openapi.generator)
}

android {
    namespace = "com.grupo1.deremate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grupo1.deremate"
        minSdk = 24
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}




val openApiOutputDir = file("$projectDir/src/main/java")

val downloadOpenApiSpec by tasks.registering {
    val outputFile = layout.buildDirectory.file("tmp/openapi/api.json")

    outputs.file(outputFile)

    doLast {
        val url = URL("http://localhost:8080/v3/api-docs")
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
        outputDir.set(openApiOutputDir.absolutePath)
        apiPackage.set("com.grupo1.deremate.api")
        modelPackage.set("com.grupo1.deremate.models")
        invokerPackage.set("com.grupo1.deremate.invoker")
        configOptions.set(
            mapOf(
                "library" to "jvm-retrofit2",
                "dateLibrary" to "java8"
            )
        )
    }
}

// Eliminar archivos viejos generados en src (opcional)
tasks.named("openApiGenerate").configure {
    doFirst {
        println("Limpiando archivos generados anteriores...")
        file("$projectDir/src/main/java/com/grupo1/deremate/api").deleteRecursively()
        file("$projectDir/src/main/java/com/grupo1/deremate/models").deleteRecursively()
        file("$projectDir/src/main/java/com/grupo1/deremate/invoker").deleteRecursively()
    }
}

