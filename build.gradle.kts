plugins {
    java
    application
    id("com.gradleup.shadow") version "9.3.0"
}

group = "org.adrianvictor.livingroom"
version = "1.0-SNAPSHOT"

application {
    mainClass = "org.adrianvictor.livingroom.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.51.2.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.freemarker:freemarker:2.3.32")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}