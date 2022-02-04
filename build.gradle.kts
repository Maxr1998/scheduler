import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.proguardGradle)
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        google()
    }
}

@Suppress("UnstableApiUsage")
plugins {
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.dependencyupdates)
}

group = "de.uaux"
version = "1.0.0"
val javaVersion = JavaVersion.VERSION_11

compose.desktop {
    application {
        mainClass = "scheduler"
        nativeDistributions {
            packageName = project.name
            modules("java.instrument", "java.management", "java.naming", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Exe)
        }
    }
}

sqldelight {
    database("Database") {
        packageName = "$group.${project.name}.model"
        schemaOutputDirectory = file("$buildDir/schemas/")
    }
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    register<ProGuardTask>("minify") {
        val packageUberJarForCurrentOS by getting
        dependsOn(packageUberJarForCurrentOS)
        val files = packageUberJarForCurrentOS.outputs.files
        injars(files)
        outjars(files.map { file -> File(file.parentFile, "${file.nameWithoutExtension}.min.jar") })
        val library = if (System.getProperty("java.version").startsWith("1.")) "lib/rt.jar" else "jmods"
        libraryjars("${System.getProperty("java.home")}/$library")
        configuration("proguard-rules.pro")
    }

    test {
        useJUnitPlatform()
    }

    // Configure dependency updates task
    withType<DependencyUpdatesTask> {
        gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
        rejectVersionIf {
            val candidateType = classifyVersion(candidate.version)
            val currentType = classifyVersion(currentVersion)

            val accept = when (candidateType) {
                // Always accept stable updates
                VersionType.STABLE -> true
                // Accept milestone updates for current milestone and unstable
                VersionType.MILESTONE -> currentType != VersionType.STABLE
                // Only accept unstable for current unstable
                VersionType.UNSTABLE -> currentType == VersionType.UNSTABLE
            }

            !accept
        }
    }
}