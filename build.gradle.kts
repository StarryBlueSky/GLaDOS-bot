@file:Suppress("KDocMissingDocumentation", "PublicApiImplicitType")

import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.breadmoirai.ChangeLogSupplier
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import java.nio.file.Paths
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val githubOrganizationName = "NephyProject"
val githubRepositoryName = "GLaDOS-bot"
val packageGroupId = "jp.nephy"
val packageVersion = Version(3, 0, 0)

val bintrayUsername by property()
val bintrayApiKey by property()

plugins { 
    kotlin("jvm") version "1.3.21"
    application

    // For testing
    id("com.adarshr.test-logger") version "1.6.0"
    id("build-time-tracker") version "0.11.0"

    // For publishing
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
    id("com.github.breadmoirai.github-release") version "2.2.4"

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
    val buildNumberPath = Paths.get(rootProject.buildDir.absolutePath, "build-number-${packageVersion.label}.txt")
    val buildNumber = if (Files.exists(buildNumberPath)) {
        buildNumberPath.toFile().readText().toIntOrNull()
    } else {
        null
    }?.coerceAtLeast(0)?.plus(1) ?: 1

    buildNumberPath.toFile().writeText(buildNumber.toString())

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
    apply(plugin = "com.adarshr.test-logger")
    apply(plugin = "build-time-tracker")

    repositories {
        mavenCentral()
        jcenter()

        maven(url = "https://kotlin.bintray.com/ktor")
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://kotlin.bintray.com/kotlin-eap")
    }
    
    dependencies {
        api(kotlin("stdlib-jdk8"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    /*
     * Tests
     */

    buildtimetracker {
        reporters {
            register("summary") {
                options["ordered"] = "true"
                options["shortenTaskNames"] = "false"
            }
        }
    }

    testlogger {
        theme = ThemeType.MOCHA
    }

    tasks.named<Test>("test") {
        useJUnitPlatform {
            includeEngines("spek2")
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
    
    if (isEAPBuild) {
        val jar = tasks.named<Jar>("jar").get()

        jar.destinationDir.listFiles().forEach {
            it.delete()
        }
    }

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

    implementation("io.ktor:ktor-client-apache:1.1.3")

    implementation("io.methvin:directory-watcher:0.9.3")

    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.fusesource.jansi:jansi:1.17.1")
}

application { 
    mainClassName = "jp.nephy.glados.MainKt"
}

/*
 * GitHub Release
 */

val githubToken by property()

if (hasProperty("github")) {
    githubRelease {
        token(githubToken)

        val jar = tasks.named<Jar>("jar").get()
        val assets = jar.destinationDir.listFiles { _, filename ->
            project.version.toString() in filename && filename.endsWith(".jar")
        }
        releaseAssets(*assets)

        owner(githubOrganizationName)
        repo(githubRepositoryName)

        tagName("v${project.version}")
        releaseName("v${project.version}")
        targetCommitish("master")
        draft(false)
        prerelease(false)
        overwrite(false)

        changelog(closureOf<ChangeLogSupplier> {
            currentCommit("HEAD")
            lastCommit("HEAD~10")
            options(listOf("--format=oneline", "--abbrev-commit", "--max-count=50", "graph"))
        })

        fun buildChangelog(): String {
            return try {
                changelog().call().lines().takeWhile {
                    "Version bump" !in it
                }.joinToString("\n") {
                    val (tag, message) = it.split(" ", limit = 2)
                    "| $tag | $message |"
                }
            } catch (e: Exception) {
                ""
            }
        }

        body {
            buildString {
                appendln("## Version\n")
                appendln("**Latest** GLaDOS version: [![Bintray](https://api.bintray.com/packages/nephyproject/glados/glados/images/download.svg)](https://bintray.com/nephyproject/glados/glados/_latestVersion)")
                appendln("The latest release build: `${project.version}`\n")

                appendln()

                appendln("## Changelogs\n")
                appendln("| Commits | Message |")
                appendln("|:------------:|:-----------|")
                append(buildChangelog())
            }
        }
    }
}
