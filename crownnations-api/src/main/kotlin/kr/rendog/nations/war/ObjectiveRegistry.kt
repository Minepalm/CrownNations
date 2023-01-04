package kr.rendog.nations.war

interface ObjectiveRegistry {

    operator fun get(monumentId: Int): MonumentObjective

}