package com.minepalm.nations.war

interface NationShieldRegistry {

    operator fun get(nationId: Int): NationShield
}