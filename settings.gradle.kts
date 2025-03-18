pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        //jcenter()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        //jcenter()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "iTranz"

// Include frontend and backend modules
include(":frontend:app") // Corrected frontend module path
include(":backend") // Keep backend module
