import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Dependencies.Plugins.kotlinJvm) version Dependencies.Versions.kotlin
    id(Dependencies.Plugins.compose) version Dependencies.Versions.composePlugin
    id(Dependencies.Plugins.sqlDelight) version Dependencies.Versions.sqlDelightPlugin
}

group = "de.uaux"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    // Core
    implementation(Dependencies.Core.koin)
    implementation(Dependencies.Core.coroutines)

    // UI
    implementation(compose.desktop.currentOs)

    // Tests
    testImplementation(kotlin("test-junit5"))
    testImplementation(Dependencies.Testing.junit)
    testRuntimeOnly(Dependencies.Testing.junitRuntime)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_14.toString()
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = rootProject.name
        }
    }
}
