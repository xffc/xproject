dependencies {
    implementation(kotlin("reflect"))
    api(project(":core"))
    implementation(libs.serialization.json)
    api(libs.ktor.network)

    implementation(libs.gson)

    api(libs.adventure.api) {
        exclude("com.google.code.gson")
    }
    api(libs.adventure.gson)
    api(libs.adventure.plain)
}