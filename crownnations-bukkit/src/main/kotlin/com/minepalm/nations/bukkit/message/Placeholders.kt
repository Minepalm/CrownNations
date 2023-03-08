package com.minepalm.nations.bukkit.message

import com.minepalm.nations.utils.TextMetadata

object Placeholders {

    val PLAYER_NAME = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%player%", text["player"])
        }
    }

    val TARGET_NAME = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%target%", text["target"])
        }
    }

    val NATION_NAME = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%nation%", text["nation"])
        }
    }

    val NATION_OWNER = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%owner%", text["owner"])
        }
    }

    val NATION_MEMBERS = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%members%", text["members"])
        }
    }

    val NATION_OFFICERS = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%officers%", text["officers"])
        }
    }

    val MONEY = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%money%", text["money"])
        }
    }

    val RANK = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%rank%", text["rank"])
        }
    }


    val AMOUNT = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            return origin.replace("%amount%", text["amount"])
        }
    }

    val ERROR = object : Replace {
        override fun replace(origin: String, text: TextMetadata): String {
            val operationResultCode = text["code"]
            val msg = text["message"]
            val error = text["error"]
            val replacement = "operationCode: $operationResultCode, message: $msg, error: $error"
            return origin.replace("%error%", replacement)
        }
    }

}