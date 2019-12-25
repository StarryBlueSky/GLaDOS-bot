rootProject.name = "glados"

pluginManagement {
    repositories {
        mavenCentral()
        jcenter()
        gradlePluginPortal()
        maven(url = "https://kotlin.bintray.com/kotlin-eap")
        maven(url = "https://plugins.gradle.org/m2")
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.jfrog.bintray" -> {
                    useModule("com.jfrog.bintray.gradle:gradle-bintray-plugin:${requested.version}")
                }
                "org.jetbrains.dokka" -> {
                    useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
                }
            }
        }
    }
}

include(":glados-api")
include(":glados-client")

rootProject.projectDir.resolve("glados-client").listFiles { file ->
    file.isDirectory && file.name.startsWith("glados-client-")
}!!.forEach {
    include(":glados-client:${it.name}")
}
