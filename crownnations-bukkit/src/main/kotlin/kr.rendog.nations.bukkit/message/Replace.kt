package kr.rendog.nations.bukkit.message

@FunctionalInterface
interface Replace {

    fun replace(origin: String, message: ResultMessage) : String{
        return origin
    }

}