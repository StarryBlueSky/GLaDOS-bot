dependencies {
    api(project(":glados-api"))
    api(kotlin("reflect"))

    implementation("io.github.microutils:kotlin-logging:1.7.8")
    api("io.ktor:ktor-client-apache:1.3.0-rc")
}

subprojects {
    dependencies {
        api(project(":glados-client"))
    }
}
