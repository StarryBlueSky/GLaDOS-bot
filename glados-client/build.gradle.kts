dependencies {
    api(project(":glados-api"))

    implementation("io.github.microutils:kotlin-logging:1.6.26")
    implementation("io.ktor:ktor-client-apache:1.2.0")
}

subprojects {
    dependencies {
        api(project(":glados-client"))
    }
}
