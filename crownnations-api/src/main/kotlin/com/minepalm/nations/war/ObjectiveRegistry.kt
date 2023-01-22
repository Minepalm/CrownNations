package com.minepalm.nations.war

interface ObjectiveRegistry {

    operator fun get(monumentId: Int): MonumentObjective

}