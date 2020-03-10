import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api("net.dv8tion:JDA:4.1.1_110")
    api("com.sedmelluq:lavaplayer:1.3.32")
    implementation("com.sedmelluq:jda-nas:1.1.0")
    // api("com.github.FredBoat:Lavalink-Client:4.0")
    // api("club.minnced:jda-reactor:1.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xallow-result-return-type"
    }
}
