package kr.rendog.nations.war

interface NationRatingRegistry {

    operator fun get(nationId: Int): NationRating

}