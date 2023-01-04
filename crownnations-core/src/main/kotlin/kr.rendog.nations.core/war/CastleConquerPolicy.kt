package kr.rendog.nations.core.war

import kr.rendog.nations.territory.ModifyPolicy
import kr.rendog.nations.territory.NationAction
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.utils.ServerLoc
import java.util.*

class CastleConquerPolicy : ModifyPolicy{
    override fun test(action: NationAction, actor: UUID, loc: ServerLoc, monument: NationMonument): Boolean {
        TODO("Not yet implemented")
    }
}