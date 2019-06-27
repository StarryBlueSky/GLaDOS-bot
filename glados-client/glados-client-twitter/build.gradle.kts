repositories {
    maven(url = "https://dl.bintray.com/nephyproject/stable")
    maven(url = "https://dl.bintray.com/nephyproject/dev")
}

dependencies {
    api("jp.nephy:penicillin:4.2.3-eap-20")
    
    api("io.ktor:ktor-client-apache:1.2.2")
    api("io.ktor:ktor-client-cio:1.2.2")
}
