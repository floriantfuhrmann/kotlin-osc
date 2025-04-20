plugins {
    kotlin("jvm") version "2.1.20"
    id("maven-publish")
}

group = "eu.florian_fuhrmann.kotlin_osc"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "eu.florian_fuhrmann.kotlin_osc"
            artifactId = "kotlin_osc"
            version = "1.0"

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}