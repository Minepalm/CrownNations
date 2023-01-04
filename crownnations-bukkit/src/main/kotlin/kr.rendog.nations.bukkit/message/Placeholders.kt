package kr.rendog.nations.bukkit.message

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

    val RANK = object : Replace {
        override fun replace(origin: String, message: ResultMessage): String {
            return origin.replace("%rank%", message["rank"])
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