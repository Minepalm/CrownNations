package kr.rendog.nations.territory

import kr.rendog.nations.utils.ServerLoc
import java.util.*

interface ModifyPolicy {

    fun test(action: NationAction, actor: UUID, loc: ServerLoc, monument: NationMonument): Boolean

}