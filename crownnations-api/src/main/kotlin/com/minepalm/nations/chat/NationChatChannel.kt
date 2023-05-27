package com.minepalm.nations.chat

import com.minepalm.nations.Nation
import com.minepalm.palmchat.api.ChatChannel

interface NationChatChannel : ChatChannel {
    
    val nation: Nation?
    
}