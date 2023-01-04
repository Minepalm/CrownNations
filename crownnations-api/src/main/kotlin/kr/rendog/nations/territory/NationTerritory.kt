package kr.rendog.nations.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

interface NationTerritory {

    val nation: Nation
    val local: Local
    val direct: Direct

    interface Local{
        fun getMonuments() : List<NationMonument>

        fun getCastles(): List<NationCastle>

        fun getCastle(monumentId: Int): NationCastle?

        fun getOutposts(): List<NationOutpost>

        fun getOutpost(monumentId: Int): NationOutpost?

        fun operateNewCastle(commander: NationMember, loc: ServerLoc): NationOperation<NationCastle>

        fun operateNewOutpost(commander: NationMember, loc: ServerLoc): NationOperation<NationOutpost>

        fun operateDisbandCastle(commander: NationMember, castle: NationCastle, reason: String): NationOperation<Boolean>

        fun operateDisbandOutpost(commander: NationMember, outpost: NationOutpost, reason: String): NationOperation<Boolean>
    }

    interface Direct{
        fun getMonuments() : CompletableFuture<List<NationMonument>>

        fun getCastles() : CompletableFuture<List<NationCastle>>

        fun getCastlesCount(): CompletableFuture<Int>

        fun getOutpostCount(): CompletableFuture<Int>

        fun getDefaultCastle() : CompletableFuture<NationCastle?>

        fun getOutposts() : CompletableFuture<List<NationOutpost>>
    }

}