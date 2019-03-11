dependencies {
    api(project(":glados-api"))

    implementation("io.github.microutils:kotlin-logging:1.6.25")
}

subprojects {
    dependencies {
        api(project(":glados-client"))
    }
    
    task<Jar>("jarClient") {
        version = ""
        destinationDir = rootDir.resolve("clients")
        
        doFirst {
            from(configurations.runtimeClasspath.filter {
                !it.name.endsWith(".pom")
            }.map {
                if (it.isDirectory) it else zipTree(it)
            })
        }
    }
}

task("jarClients") {
    for (project in subprojects) {
        dependsOn(project.tasks["jarClient"])
    }
}
