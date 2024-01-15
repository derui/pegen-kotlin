plugins {
    kotlin("jvm") version "1.9.21"
    `java-library`
    `maven-publish`
}

group = "io.github.derui"
version = "0.1.0"

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
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/derui/pegen-kotlin")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    
    publications {
        create<MavenPublication>("grp") {
            artifactId = "pegen-kotlin-core"
            version = "0.1.0"
            from(components["java"])
        }
    }
}
