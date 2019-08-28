import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"
}

group = "io.github.potatocurry"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:1.7.19")
    implementation("io.ktor:ktor-client-cio:1.2.3")
    implementation("io.ktor:ktor-client-websockets:1.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.12.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
