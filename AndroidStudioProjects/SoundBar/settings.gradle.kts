pluginManagement {
    repositories {
        google()  // For Android Gradle plugins
        mavenCentral()  // For additional plugins and dependencies
        gradlePluginPortal()  // To fetch plugins from the Gradle Plugin Portal
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)  // Ensures repositories are managed centrally
    repositories {
        google()  // For Android dependencies
        mavenCentral()  // For other dependencies
    }
}

rootProject.name = "SoundBar"
include(":app")  // Include the app module
