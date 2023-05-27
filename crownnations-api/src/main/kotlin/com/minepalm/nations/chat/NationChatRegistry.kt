package com.minepalm.nations.chat

import com.minepalm.nations.Nation

interface NationChatRegistry {

    operator fun get(nation: Nation): NationChat

    operator fun get(id: Int): NationChat?

}