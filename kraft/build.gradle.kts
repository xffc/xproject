dependencies {
    implementation(kotlin("reflect"))
    api(project(":core"))
    implementation(libs.serialization.json)
    api(libs.ktor.network)
}