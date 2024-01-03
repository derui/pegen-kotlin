plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "pegen-kotlin"

include("pegen-kotlin-core")
include("pegen-kotlin-sample")
