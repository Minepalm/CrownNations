package com.minepalm.nations.territory

import com.minepalm.nations.utils.ServerLoc

data class MonumentSchema(
    val id: Int,
    val nationId: Int,
    val type: String,
    val center: ServerLoc,
    val range: ProtectionRange,
)