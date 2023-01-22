package com.minepalm.nations.territory

data class MonumentSchema(
    val id: Int,
    val nationId: Int,
    val type: String,
    val center: com.minepalm.nations.utils.ServerLoc,
    val range: ProtectionRange,
)