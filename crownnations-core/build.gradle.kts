group = rootProject.group
version = rootProject.version

dependencies{
    implementation(project(":crownnations-api"))

    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.google.code.gson:gson:2.9.1")
}