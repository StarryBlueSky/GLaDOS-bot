project(":workspace:common").subprojects {
    dependencies {
        project(":glados-client").dependencyProject.subprojects.forEach {
            compileOnly(it)
        }
    }
}

project(":workspace:projects").subprojects {
    dependencies {
        project(":glados-client").dependencyProject.subprojects.forEach {
            compileOnly(it)
        }
    }

    project(":workspace:common").subprojects.forEach {
        dependencies {
            compileOnly(it)
        }
    }
}

/**
 * Jar Task
 */
project(":workspace:common").subprojects {
    tasks.getByName<Jar>("jar") {
        destinationDirectory.set(rootDir.resolve("workspace/libs"))

        doFirst {
            from(configurations.runtimeClasspath.filter {
                !it.name.endsWith(".pom")
            }.map {
                if (it.isDirectory) it else zipTree(it)
            })
        }
    }
}

project(":workspace:projects").subprojects {
    tasks.getByName<Jar>("jar") {
        destinationDirectory.set(rootDir.resolve("workspace/plugins"))

        doFirst {
            from(configurations.runtimeClasspath.filter {
                !it.name.endsWith(".pom")
            }.map {
                if (it.isDirectory) it else zipTree(it)
            })
        }
    }
}

dependencies {
    project(":glados-client").dependencyProject.subprojects.forEach {
        implementation(it)
    }

    implementation(rootProject)
}

/**
 * Workspace Task
 */
task("buildWorkspace") {
    dependsOn(rootProject.tasks["build"])

    dependsOn(tasks["jar"])

    project(":workspace:common").subprojects.forEach {
        dependsOn(it.tasks.withType<Jar>())
    }

    project(":workspace:projects").subprojects.forEach {
        dependsOn(it.tasks.withType<Jar>())
    }
}

/**
 * Runtime Task
 */
task<Exec>("runProduction") {
    dependsOn(tasks["buildWorkspace"])

    workingDir = rootDir.resolve("workspace")
    commandLine = listOf("java", "-jar", "glados.jar")
}

task<Exec>("runDevelopment") {
    dependsOn(tasks["buildWorkspace"])

    workingDir = rootDir.resolve("workspace")
    commandLine = listOf("java", "-jar", "glados.jar", "--dev")
}

tasks.getByName<Jar>("jar") {
    archiveFileName.set("glados.jar")
    destinationDirectory.set(rootDir.resolve("workspace"))

    manifest {
        attributes("Main-Class" to "jp.nephy.glados.MainKt")
    }

    doFirst {
        from(configurations.compileClasspath.filter {
            !it.name.endsWith(".pom")
        }.map {
            if (it.isDirectory) it else zipTree(it)
        })
    }
}
