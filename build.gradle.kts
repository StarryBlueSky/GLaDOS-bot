@file:Suppress("KDocMissingDocumentation", "PublicApiImplicitType")

import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val githubOrganizationName = "NephyProject"
val githubRepositoryName = "GLaDOS-bot"
val packageGroupId = "jp.nephy"
val packageVersion = Version(3, 0, 0)

val bintrayUsername by property()
val bintrayApiKey by property()

plugins { 
    kotlin("jvm") version "1.3.31"
    
    // For publishing
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"

    // For documentation
    id("org.jetbrains.dokka") version "0.9.17"
}

fun Project.property(key: String? = null) = object: ReadOnlyProperty<Project, String?> {
    override fun getValue(thisRef: Project, property: KProperty<*>): String? {
        val name = key ?: property.name
        return (properties[name] ?: System.getProperty(name) ?: System.getenv(name))?.toString()
    }
}

/*
 * Versioning
 */

data class Version(val major: Int, val minor: Int, val patch: Int) {
    val label: String
        get() = "$major.$minor.$patch"
}

val isEAPBuild: Boolean
    get() = hasProperty("snapshot")

fun incrementBuildNumber(): Int {
    val buildNumberFile = rootProject.buildDir.resolve("build-number-${packageVersion.label}.txt")
    val buildNumber = if (buildNumberFile.exists()) {
        buildNumberFile.readText().toIntOrNull()
    } else {
        null
    }?.coerceAtLeast(0)?.plus(1) ?: 1

    buildNumberFile.writeText(buildNumber.toString())

    return buildNumber
}

val currentVersion = if (isEAPBuild) {
    "${packageVersion.label}-eap-${incrementBuildNumber()}"
} else {
    packageVersion.label
}

allprojects {
    group = packageGroupId
    version = currentVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        jcenter()

        maven(url = "https://kotlin.bintray.com/ktor")
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://kotlin.bintray.com/kotlin-eap")
    }
    
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    /*
     * Documentation
     */

    apply(plugin = "org.jetbrains.dokka")
    
    tasks.named<DokkaTask>("dokka") {
        outputFormat = "html"
        outputDirectory = "${rootProject.buildDir}/kdoc"

        jdkVersion = 8
        includeNonPublic = false
        reportUndocumented = true
        skipEmptyPackages = true
        skipDeprecated = true
    }

    /*
     * Publishing
     */

    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.bintray")

    val sourcesJar = task<Jar>("sourcesJar") {
        classifier = "sources"
        from(sourceSets["main"].allSource)
    }

    publishing {
        publications {
            create<MavenPublication>("kotlin") {
                from(components.getByName("java"))
                
                artifact(sourcesJar)
            }
        }
    }

    bintray {
        setPublications("kotlin")

        user = bintrayUsername
        key = bintrayApiKey
        publish = true
        override = true

        pkg.apply {
            repo = githubRepositoryName.toLowerCase()
            userOrg = githubOrganizationName.toLowerCase()

            name = project.name
            desc = project.description

            setLicenses("MIT")
            publicDownloadNumbers = true

            githubRepo = "$githubOrganizationName/$githubRepositoryName"
            websiteUrl = "https://github.com/$githubOrganizationName/$githubRepositoryName"
            issueTrackerUrl = "https://github.com/$githubOrganizationName/$githubRepositoryName/issues"
            vcsUrl = "https://github.com/$githubOrganizationName/$githubRepositoryName.git"

            version.apply {
                name = project.version.toString()
                desc = "${project.name} ${project.version}"
                released = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(Date())
                vcsTag = project.version.toString()
            }
        }
    }

    tasks.withType<BintrayUploadTask> {
        dependsOn("publishToMavenLocal")
    }
}

dependencies {
    implementation(project(":glados-client"))

    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-client-apache:1.2.0")

    implementation("io.methvin:directory-watcher:0.9.4")

    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.fusesource.jansi:jansi:1.17.1")
}
