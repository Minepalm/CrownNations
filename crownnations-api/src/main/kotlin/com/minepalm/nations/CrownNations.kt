package com.minepalm.nations

object CrownNations {

    val inst: NationService
        get() = Dependencies[NationService::class.java].get()

}