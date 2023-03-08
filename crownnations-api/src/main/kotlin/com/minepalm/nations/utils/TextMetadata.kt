package com.minepalm.nations.utils

interface TextMetadata {
    operator fun get(key: String): String
    operator fun set(key: String, value: String)
}
