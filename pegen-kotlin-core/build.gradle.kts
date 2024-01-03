plugins {
    kotlin("jvm") version "1.9.21"
    `java-library`
    `maven-publish`
}

group = "io.github.derui"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks {
    jar {}
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            groupId = "io.github.derui"
            artifactId = "pegen-kotlin-core"
            version = "0.0.1"
            from(components["java"])
        }
    }
}
