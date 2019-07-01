dependencies {
    api(project(":glados-api"))
    
    api("io.ktor:ktor-client-apache:1.2.2")
    implementation("io.github.microutils:kotlin-logging:1.6.26")
}

subprojects {
    dependencies {
        api(project(":glados-client"))
    }
}
