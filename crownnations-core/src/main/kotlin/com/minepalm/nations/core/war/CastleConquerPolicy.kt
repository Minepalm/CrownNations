package com.minepalm.nations.core.war

import com.minepalm.nations.territory.ModifyPolicy
import com.minepalm.nations.territory.NationAction
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.utils.ServerLoc
import java.util.*

class CastleConquerPolicy : ModifyPolicy {
    override fun test(
        action: NationAction,
        actor: UUID,
        loc: ServerLoc,
        monument: NationMonument
    ): Boolean {
        TODO("Not yet implemented")
    }
}