pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://dl.google.com/dl/android/maven2") }
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "WanCompose"
include(":app")
 