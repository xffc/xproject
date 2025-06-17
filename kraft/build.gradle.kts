dependencies {
    implementation(kotlin("reflect"))
    api(project(":core"))
    implementation(libs.serialization.json)
    api(libs.ktor.network)

    api(libs.adventure.api)
    api(libs.adventure.gson)
    api(libs.adventure.plain)
}