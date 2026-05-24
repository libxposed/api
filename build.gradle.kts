val dependencySnapshot = providers.gradleProperty("dependencySnapshot").orNull == "true"

allprojects {
    configurations.configureEach {
        if (dependencySnapshot) {
            resolutionStrategy.cacheChangingModulesFor(0, "seconds")
        }

    }
}

tasks.register("Delete", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
