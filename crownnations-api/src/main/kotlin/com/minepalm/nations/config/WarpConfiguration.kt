package com.minepalm.nations.config

import com.minepalm.nations.utils.WarpOffset

interface WarpConfiguration {

    fun getDefaultMonumentOffset(monumentType: String): WarpOffset

    fun getWarpDelay(): Int

}