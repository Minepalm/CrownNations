package kr.rendog.nations.territory

import kr.rendog.nations.utils.ServerLoc

data class MonumentSchema(
    val id: Int,
    val nationId: Int,
    val type: String,
    val center: ServerLoc,
    val range: ProtectionRange,
)