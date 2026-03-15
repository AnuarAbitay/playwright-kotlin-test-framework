plugins {
    kotlin("jvm") version "2.2.20"
    id("io.qameta.allure") version "2.12.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val allureVersion = "2.29.0"
val aspectJVersion = "1.9.22.1"

dependencies {
    implementation("com.microsoft.playwright:playwright:1.55.0")
    implementation("org.junit.jupiter:junit-jupiter:5.11.4")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    implementation("org.assertj:assertj-core:3.27.7")

    testImplementation(platform("io.qameta.allure:allure-bom:$allureVersion"))
    testImplementation("io.qameta.allure:allure-junit5")
}

allure {
    adapter {
        allureJavaVersion.set(allureVersion)
        aspectjWeaver.set(true)
        frameworks {
            junit5 {
                enabled.set(true)
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}