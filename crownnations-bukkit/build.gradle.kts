plugins {
    id("java")
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("io.papermc.paperweight.userdev")
}

apply(plugin = "io.papermc.paperweight.userdev")

group = rootProject.group
version = rootProject.version


repositories{
    maven{
        name = "enginehub"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.minepalm:PalmLibrary-Bukkit:1.0-SNAPSHOT")
    compileOnly("com.minepalm:palmchat-core:1.19-1.1-SNAPSHOT")
    compileOnly("com.minepalm:PalmCoconut:1.0-SNAPSHOT")

    implementation(project(":crownnations-api"))
    implementation(project(":crownnations-core"))

    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT")
    implementation("com.minepalm:arkarangutils-bukkit:1.2-SNAPSHOT")
    implementation("com.minepalm:arkarangutils-invitation:1.2-SNAPSHOT")

    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT"){ isTransitive = true }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT"){ isTransitive = true }
}


tasks.jar {
    dependsOn("reobfJar")
}

tasks.reobfJar {
    outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
}
