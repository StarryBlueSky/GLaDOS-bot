dependencies {
    api(project(":glados-api"))

    implementation("io.github.microutils:kotlin-logging:1.6.25")
}

subprojects {
    dependencies {
        api(project(":glados-client"))
    }
}
