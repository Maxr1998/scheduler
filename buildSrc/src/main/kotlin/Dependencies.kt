object Dependencies {
    object Versions {
        const val kotlin = "1.4.20"
        const val composePlugin = "0.2.0-build132"
        const val sqlDelightPlugin = "1.4.4"
        const val dependencyUpdatesPlugin = "0.36.0"

        // Core
        const val koin = "2.2.2"
        const val coroutines = "1.4.2"

        // Testing
        const val junit = "5.7.0"
    }

    object Plugins {
        const val kotlinJvm = "jvm"
        const val compose = "org.jetbrains.compose"
        const val sqlDelight = "com.squareup.sqldelight"
        const val dependencyUpdates = "com.github.ben-manes.versions"
    }

    object Core {
        const val koin = "org.koin:koin-core:${Versions.koin}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    }

    object Testing {
        const val junit = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
        const val junitRuntime = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    }
}