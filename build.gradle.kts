import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("org.jetbrains.compose") version "0.3.2"
}

group = "sample.gmo"
version = "1.0"



repositories {
    google()
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven { url = uri("https://dl.bintray.com/kodein-framework/Kodein-DB") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.kodein.db:kodein-db-jvm:0.8.1-beta")
    implementation("org.kodein.db:kodein-db-serializer-kotlinx:0.8.1-beta")
    implementation("org.kodein.db:kodein-leveldb-jni-jvm-windows:0.8.1-beta")

    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    implementation("io.ktor:ktor-auth:1.6.3")

    implementation(kotlin("stdlib-jdk8"))

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ScrumboardCompose"
            packageVersion = "1.0.0"
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}