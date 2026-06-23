plugins {
    kotlin("jvm") version "2.3.20"
    id("io.qameta.allure") version "4.0.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.14.4"
val allureAdapterVersion = "2.35.2"

dependencies {
    implementation("com.microsoft.playwright:playwright:1.55.0")

    implementation(platform("org.junit:junit-bom:$junitVersion"))
    implementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.6")

    implementation("org.assertj:assertj-core:3.27.7")

    implementation(platform("io.qameta.allure:allure-bom:$allureAdapterVersion"))
    implementation("io.qameta.allure:allure-java-commons")
    testImplementation("io.qameta.allure:allure-junit5")
}

allure {
    version.set("3.9.0")

    adapter {
        allureJavaVersion.set(allureAdapterVersion)
        aspectjWeaver.set(false)
        autoconfigureListeners.set(true)

        frameworks {
            junit5 {
                enabled.set(true)
                autoconfigureListeners.set(true)
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()

    systemProperty(
        "headless",
        System.getProperty("headless", "true")
    )

    systemProperty(
        "browser",
        System.getProperty("browser", "CHROMIUM")
    )
}

kotlin {
    jvmToolchain(25)
}