import java.util.Properties
import java.io.FileInputStream

// Lê o local.properties pra pegar as credenciais do Supabase sem
// deixá-las escritas direto no código-fonte.
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.snacklapaz.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.snacklapaz.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String", "SUPABASE_URL",
            "\"${localProperties.getProperty("SUPABASE_URL", "")}\""
        )
        buildConfigField(
            "String", "SUPABASE_ANON_KEY",
            "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\""
        )
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
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // ===== Base do projeto (geradas pelo Android Studio) =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ===== Ícones estendidos (Material Symbols/Icons completos) =====
    implementation("androidx.compose.material:material-icons-extended")

    // ===== Navegação =====
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // ===== Splash Screen API nativa do Android 12+ =====
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ===== Animações extras do Compose =====
    implementation("androidx.compose.animation:animation")

    // ===== ViewModel + lifecycle no Compose =====
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // ===== Carregamento de imagens (fotos de produtos do Supabase Storage) =====
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")

    // ===== Supabase (BOM controla a versao de todos os modulos juntos) =====
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.5"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")

    // ===== Ktor (motor de rede usado por baixo pelo Supabase) =====
    implementation("io.ktor:ktor-client-android:3.2.1")

    // ===== Serializacao (JSON <-> objetos Kotlin) =====
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ===== Necessario por causa do minSdk 24 (Supabase 3.x exige 26+) =====
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
}
