package kr.rendog.nations.war

interface NationShieldRegistry {

    operator fun get(nationId: Int): NationShield
}