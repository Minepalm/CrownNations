package com.minepalm.nations.war

interface NationRatingRegistry {

    operator fun get(nationId: Int): NationRating

}