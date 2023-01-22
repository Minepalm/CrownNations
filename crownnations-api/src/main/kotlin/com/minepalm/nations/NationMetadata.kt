package com.minepalm.nations

import java.util.concurrent.CompletableFuture

interface NationMetadata {

    val modifier: Modifier

    interface Modifier {

        fun set(key: String, value: String?): CompletableFuture<Unit>

        fun get(key: String): CompletableFuture<String?>

        fun remove(key: String): CompletableFuture<Unit>

        fun getAll(): CompletableFuture<Map<String, String>>

    }

    fun get(key: String): String?

    fun get(key: String, defaultValue: String): String

    fun getInt(key: String): Int?

    fun getInt(key: String, defaultValue: Int): Int

    fun getDouble(key: String): Double?

    fun getDouble(key: String, defaultValue: Double): Double

    fun getBoolean(key: String): Boolean?

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun set(key: String, value: String?)

    fun set(key: String, value: Int)

    fun set(key: String, value: Double)

    fun set(key: String, value: Boolean)

    fun remove(key: String)

    fun sync(): CompletableFuture<Unit>

    fun sync(key: String): CompletableFuture<Unit>
}