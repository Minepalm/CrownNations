package com.minepalm.nations.bukkit.message

import com.minepalm.nations.utils.TextMetadata

@FunctionalInterface
interface Replace {
    fun replace(origin: String, data: TextMetadata) : String{
        return origin
    }

}