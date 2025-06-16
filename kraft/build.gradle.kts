dependencies {
    implementation(kotlin("reflect"))
    api(project(":core"))
    implementation(libs.serialization.json)
    api(libs.ktor.network)

    api(libs.adventure.api)
    implementation(libs.adventure.gson)
    implementation(libs.adventure.plain)
}