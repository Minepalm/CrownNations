group = rootProject.group
version = rootProject.version

repositories{
    maven{
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    implementation("com.minepalm:arkarangutils-bungee:1.2-SNAPSHOT")

    implementation(project(":crownnations-api"))
    implementation(project(":crownnations-core"))
}
