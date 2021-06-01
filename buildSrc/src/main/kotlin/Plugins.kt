object Plugins {
    const val proguardGradle = "com.guardsquare:proguard-gradle:${Versions.proguardGradle}"
    const val kotlinJvm = "jvm"
    const val compose = "org.jetbrains.compose"
    const val sqlDelight = "com.squareup.sqldelight"
    const val dependencyUpdates = "com.github.ben-manes.versions"

    object Versions {
        const val proguardGradle = "7.1.0-beta5"
        const val kotlin = "1.5.10"
        const val composePlugin = "0.4.0"
        const val sqlDelight = "1.5.0"
        const val dependencyUpdatesPlugin = "0.38.0"
    }
}