plugins {
    id("java")
}

group = "com.minepalm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.minepalm:palmchat-core:1.19-1.1-SNAPSHOT")
    compileOnly(project(":crownnations-api"))
    compileOnly(project(":crownnations-core"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}