plugins {
}

version = "1.0"

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":core"))
    implementation(libs.serialization.json)
    implementation(libs.ktor.network)
}