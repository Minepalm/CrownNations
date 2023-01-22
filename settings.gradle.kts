pluginManagement {
    val myNexusUsername: String by settings
    val myNexusPassword: String by settings

    repositories {
        gradlePluginPortal()
        maven {
            name = "minepalm-snapshots"
            url = uri("https://nexus.minepalm.com/repository/maven-snapshots")
            credentials {
                username = myNexusUsername
                password = myNexusPassword
            }
        }
    }
}

rootProject.name = "CrownNations"
include("crownnations-api")
include("crownnations-core")
include("crownnations-territories")
include("crownnations-war")
include("crownnations-bukkit")
include("crownnations-bungee")
//include("crownnations-chat")
