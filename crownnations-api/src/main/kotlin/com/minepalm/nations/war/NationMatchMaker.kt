package com.minepalm.nations.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationMatchMaker {

    fun operateMatchMake(commander: NationMember, type: WarType, nation: Nation, other: Nation)
            : NationOperation<WarSession>

    fun createNewMatch(info: WarInfo): CompletableFuture<WarSession>

}