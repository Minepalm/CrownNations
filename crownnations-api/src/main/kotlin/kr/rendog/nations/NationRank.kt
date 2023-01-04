package kr.rendog.nations

enum class NationRank(
    val weight : Int
) {

    NONE(-1),
    RESIDENT(0),
    SENIOR(10),
    OFFICER(20),
    OWNER(50),
    ADMIN(100),
    SYSTEM(101);

    object Finder{

        val map = mutableMapOf<Int, NationRank>()

        fun by(weight : Int) : NationRank{
            return map[weight] ?: NONE
        }

    }
    init{
        Finder.map[weight] = this
    }

    fun hasPermissibleOf(rank : NationRank) : Boolean{
        return this.weight >= rank.weight
    }
}