object Plugins {
    const val proguardGradle = "com.guardsquare:proguard-gradle:${Versions.proguardGradle}"
    const val kotlinJvm = "jvm"
    const val compose = "org.jetbrains.compose"
    const val sqlDelight = "com.squareup.sqldelight"
    const val dependencyUpdates = "com.github.ben-manes.versions"

    object Versions {
        const val proguardGradle = "7.1.0"
        const val kotlin = "1.5.21"
        const val composePlugin = "0.5.0-build270"
        const val sqlDelight = "1.5.0"
        const val dependencyUpdatesPlugin = "0.39.0"
    }
}