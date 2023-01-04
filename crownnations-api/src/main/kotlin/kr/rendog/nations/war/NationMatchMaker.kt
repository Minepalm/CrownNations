package kr.rendog.nations.war

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationMatchMaker {

    fun operateMatchMake(commander: NationMember, type: WarType, nation: Nation, other: Nation)
    : NationOperation<WarSession>

    fun createNewMatch(info: WarInfo): CompletableFuture<WarSession>

}