import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Dependencies.Plugins.kotlinJvm) version Dependencies.Versions.kotlin
    id(Dependencies.Plugins.compose) version Dependencies.Versions.composePlugin
    id(Dependencies.Plugins.sqlDelight) version Dependencies.Versions.sqlDelight
    id(Dependencies.Plugins.dependencyUpdates) version Dependencies.Versions.dependencyUpdatesPlugin
}

group = "de.uaux"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    // Core
    implementation(Dependencies.Core.koin)
    implementation(Dependencies.Core.coroutines)

    // UI
    implementation(compose.desktop.currentOs)
    implementation(compose("org.jetbrains.compose.material:material-icons-extended"))

    // Persistence
    implementation(Dependencies.Persistence.sqlDelightSqliteDriver)
    implementation(Dependencies.Persistence.sqlDelightCoroutinesExtension)

    // Logging
    implementation(Dependencies.Logging.kotlinLogging)
    implementation(Dependencies.Logging.logback)

    // Tests
    testImplementation(kotlin("test-junit5"))
    testImplementation(Dependencies.Testing.junit)
    testRuntimeOnly(Dependencies.Testing.junitRuntime)
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