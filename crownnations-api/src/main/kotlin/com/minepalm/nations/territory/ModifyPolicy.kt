package com.minepalm.nations.territory

import java.util.*

interface ModifyPolicy {

    fun test(
        action: NationAction,
        actor: UUID,
        loc: com.minepalm.nations.utils.ServerLoc,
        monument: NationMonument
    ): Boolean

}