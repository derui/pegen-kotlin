plugins {
    kotlin("jvm") version "1.9.21"
}

group = "io.github.derui"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":pegen-kotlin-core"))
}

kotlin {
    jvmToolchain(17)
}