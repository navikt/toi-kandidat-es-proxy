plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "no.nav.toi.kandidatesproxy.ApplicationKt"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("io.javalin:javalin:4.1.1")
    implementation("net.logstash.logback:logstash-logback-encoder:7.0.1")
    implementation("no.nav.security:mock-oauth2-server:0.3.6")
    implementation("no.nav.security:token-validation-core:1.3.9")

    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mock-server:mockserver-netty:5.11.2")
}
