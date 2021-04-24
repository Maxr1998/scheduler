import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.kotlinJvm) version Plugins.Versions.kotlin
    id(Plugins.compose) version Plugins.Versions.composePlugin
    id(Plugins.sqlDelight) version Plugins.Versions.sqlDelight
    id(Plugins.dependencyUpdates) version Plugins.Versions.dependencyUpdatesPlugin
}

group = "de.uaux"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    // Core
    implementation(libs.koin)
    implementation(libs.coroutines)

    // UI
    implementation(compose.desktop.currentOs)
    implementation(compose("org.jetbrains.compose.material:material-icons-extended"))

    // Persistence
    implementation(libs.bundles.sqlDelight)

    // Logging
    implementation(libs.bundles.logging)

    // Tests
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.runtime)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_14.toString()
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        useIR = true
    }
}

sqldelight {
    database("Database") {
        packageName = "$group.${project.name}.model"
    }
}

compose.desktop {
    application {
        mainClass = "scheduler"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = project.name
        }
    }
}

fun isStable(version: String): Boolean {
    return listOf("alpha", "beta", "dev", "rc").none { version.toLowerCase().contains(it) }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        !isStable(candidate.version) && isStable(currentVersion)
    }
}