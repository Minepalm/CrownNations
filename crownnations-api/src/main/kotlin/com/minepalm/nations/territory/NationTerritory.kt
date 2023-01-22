package com.minepalm.nations.territory

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationTerritory {

    val nation: Nation
    val local: Local
    val direct: Direct

    interface Local {
        fun getMonuments(): List<NationMonument>

        fun getCastles(): List<NationCastle>

        fun getCastle(monumentId: Int): NationCastle?

        fun getOutposts(): List<NationOutpost>

        fun getOutpost(monumentId: Int): NationOutpost?

        fun operateNewCastle(
            commander: NationMember,
            loc: com.minepalm.nations.utils.ServerLoc
        ): NationOperation<NationCastle>

        fun operateNewOutpost(
            commander: NationMember,
            loc: com.minepalm.nations.utils.ServerLoc
        ): NationOperation<NationOutpost>

        fun operateDisbandCastle(
            commander: NationMember,
            castle: NationCastle,
            reason: String
        ): NationOperation<Boolean>

        fun operateDisbandOutpost(
            commander: NationMember,
            outpost: NationOutpost,
            reason: String
        ): NationOperation<Boolean>
    }

    interface Direct {
        fun getMonuments(): CompletableFuture<List<NationMonument>>

        fun getCastles(): CompletableFuture<List<NationCastle>>

        fun getCastlesCount(): CompletableFuture<Int>

        fun getOutpostCount(): CompletableFuture<Int>

        fun getDefaultCastle(): CompletableFuture<NationCastle?>

        fun getOutposts(): CompletableFuture<List<NationOutpost>>
    }

}