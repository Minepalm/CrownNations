plugins {
    id("java")
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.4.1-SNAPSHOT" apply false
    id("maven-publish")
}

group = "com.minepalm"
version = "1.0-SNAPSHOT"

allprojects {
    apply {
        plugin("java")
        plugin("kotlin")
        plugin("com.github.johnrengelman.shadow")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        maven {
            name = "minepalm-snapshots"
            url = uri("https://nexus.minepalm.com/repository/maven-snapshots")
            credentials {
                username = project.properties["myNexusUsername"].toString()
                password = project.properties["myNexusPassword"].toString()
            }
        }
    }

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib")

        compileOnly("com.minepalm:PalmLibrary-Database:1.1-SNAPSHOT")
        compileOnly("com.minepalm:PalmLibrary-Network:1.1-SNAPSHOT")
        compileOnly("com.minepalm:PalmLibrary-dependencies:1.1-SNAPSHOT")
        compileOnly("com.minepalm:syncer-player:1.19-1.0-SNAPSHOT")
        compileOnly("com.minepalm:BungeeJump:1.19-1.0-SNAPSHOT")
        compileOnly("com.minepalm:palmchat-api:1.19-1.2.1-SNAPSHOT")

        compileOnly("net.kyori:adventure-text-minimessage:4.12.0")

        compileOnly("com.google.guava:guava:31.1-jre")
        compileOnly("io.netty:netty-buffer:4.1.86.Final")
        testImplementation(kotlin("test"))
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "minepalm-snapshots"
                url = uri("https://nexus.minepalm.com/repository/maven-snapshots")
                credentials {
                    username = project.properties["myNexusUsername"].toString()
                    password = project.properties["myNexusPassword"].toString()
                }
            }

        }

    }


    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    tasks.test {
        useJUnitPlatform()
    }
}