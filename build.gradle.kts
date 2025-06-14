plugins {
    `maven-publish`

    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization) apply false
}

group = "fun.xffc.${rootProject.name}"

val user = property("github.user") as String
val repo = property("github.repo") as String

val githubActor = System.getenv("GITHUB_ACTOR")!!
val githubToken = System.getenv("GITHUB_TOKEN")!!

repositories {
    mavenCentral()
}

subprojects {
    val libs = rootProject.libs

    group = rootProject.group
    version = property("version") as String

    repositories.addAll(rootProject.repositories)

    apply(plugin = "maven-publish")
    apply(plugin = libs.plugins.kotlin.get().pluginId)
    apply(plugin = libs.plugins.serialization.get().pluginId)

    sourceSets.main {
        java.srcDir("src")
        kotlin.srcDir("src")
        resources.srcDir("resources")
    }

    sourceSets.test {
        java.srcDir("test/src")
        kotlin.srcDir("test/src")
        resources.srcDir("resources")
    }

    kotlin {
        jvmToolchain(21)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<Jar> {
        destinationDirectory = file("$rootDir/build")
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/$user/$repo")
                credentials {
                    username = githubActor
                    password = githubToken
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
            }
        }
    }
}

tasks.jar {
    enabled = false
}

kotlin {
    jvmToolchain(21)
}