# LibXposed - API

## Add to project
1) Clone project, with [configurations](#Submodule-Configurations) as submodule; or
2) Download `.aar`s and `.jar`s from [deployments](deployments) to gradle library directory, and add to project locally; or
3) Other (Planned)

## Submodule Configurations
1) Add as submodule to Git
   ```
   git submodule add https://github.com/libxposed/api api
   git submodule init
   git submodule update
   ```
2) Configure dependencies
    1. Remove original plugins declaration
        - Remove all plugins from `build.gradle.kts`
        - Copy `libs.versions.toml` from `api/gradle` to `gradle`
        - Add following lines to `[plugins]` part
          ```
          kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
          agp-app = { id = "com.android.application", version.ref = "agp" }
          ```
    2. Update dependencies (Optional)
        - Modify values form `[versions]` in
    4. Apply declared plugins
        - Remove following plugin from `plugins` of `app/build.gradle.kts`
          ```
          id("com.android.application")
          kotlin("android")
          ```
        - Replace plugins with
          ```
          alias(libs.plugins.agp.app)
          alias(libs.plugins.kotlin.android)
          ```
    5. Declare submodule
        - Declare as submodule
            - Add following into `settings.gradle.kts` above `include(":app")`
          ```
          include(":checks", ":api")
          project(":checks").projectDir = File("./api/checks")
          project(":api").projectDir = File("./api/api")
          ```
        - Add as `compileOnly` dependency
            - Add to `app/build.gradle.kts`
          ```
          compileOnly(project(":api"))
          ```
3) Gradle Sync
