import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api("net.dv8tion:JDA:4.ALPHA.0_63")
    api("com.sedmelluq:lavaplayer:1.3.11")
    // implementation("com.sedmelluq:jda-nas:1.0.6")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xallow-result-return-type"
    }
}
