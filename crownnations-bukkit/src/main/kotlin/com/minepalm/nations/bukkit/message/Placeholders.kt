package com.minepalm.nations.bukkit.message

object Placeholders {

    val PLAYER_NAME = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%player%", message["player"])
        }
    }

    val TARGET_NAME = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%target%", message["target"])
        }
    }

    val NATION_NAME = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%nation%", message["nation"])
        }
    }

    val NATION_OWNER = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%owner%", message["owner"])
        }
    }

    val NATION_MEMBERS = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%members%", message["members"])
        }
    }

    val NATION_OFFICERS = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%officers%", message["officers"])
        }
    }

    val MONEY = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%money%", message["money"])
        }
    }

    val RANK = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%rank%", message["rank"])
        }
    }


    val AMOUNT = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%amount%", message["amount"])
        }
    }

    val ERROR = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            val operationResultCode = message.operation?.code ?: "NONE"
            val text = message.data["message"] ?: ""
            val error = message.operation?.exception ?: "없음"
            val replacement = "operationCode: $operationResultCode, message: $text, error: $error"
            return origin.replace("%error%", replacement)
        }
    }

}