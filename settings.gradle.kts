pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal {
            content {
                includeGroup("io.github.libxposed")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "libxposed-api"

include(":api")
