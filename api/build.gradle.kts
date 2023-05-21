plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.github.libxposed.api"
    compileSdk = 33
    buildToolsVersion = "33.0.2"

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard-rules.pro")
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("api") {
            artifactId = "api"
            group = "io.github.libxposed"
            version = "100"
            pom {
                name.set("api")
                description.set("Modern Xposed API")
                url.set("https://github.com/libxposed/api")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/libxposed/api/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("libxposed")
                        url.set("https://libxposed.github.io")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/libxposed/api.git")
                    url.set("https://github.com/libxposed/api")
                }
            }
            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials(PasswordCredentials::class)
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/libxposed/api")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}


dependencies {
    compileOnly("androidx.annotation:annotation:1.6.0")
}
