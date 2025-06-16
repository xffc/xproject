import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `maven-publish`

    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.mavenpublish)
}

group = "io.github.xffc.${rootProject.name}"

val githubAuthor = rootProject.property("github.author") as String
val githubProject = "$githubAuthor/${rootProject.name}"

repositories {
    mavenCentral()
}

subprojects {
    val libs = rootProject.libs

    group = rootProject.group
    version = property("version") as String
    description = property("description") as String

    repositories.addAll(rootProject.repositories)

    apply(plugin = libs.plugins.kotlin.get().pluginId)
    apply(plugin = libs.plugins.serialization.get().pluginId)
    apply(plugin = libs.plugins.mavenpublish.get().pluginId)

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

    mavenPublishing {
        coordinates(
            groupId = group as String,
            artifactId = name,
            version = version as String
        )

        pom {
            name.set(project.name)
            description.set(project.description)
            url.set("https://$githubProject")

            licenses {
                license {
                    name = "MIT"
                    url = "https://${githubProject.replace("github.com", "raw.githubusercontent.com")}/refs/heads/master/LICENSE"
                }
            }

            developers {
                developer {
                    id.set("xffcsk")
                    name.set("xffc")
                    url.set(githubAuthor)
                }
            }

            scm {
                url.set(githubProject)
                connection.set("scm:git:git://$githubProject.git")
                developerConnection.set("scm:git:ssh://$githubProject.git")
            }
        }

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

        signAllPublications()
    }

    tasks.withType<Jar> {
        destinationDirectory.set(file("$rootDir/build"))
    }
}

tasks.jar {
    enabled = false
}

kotlin {
    jvmToolchain(21)
}