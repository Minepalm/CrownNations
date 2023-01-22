package com.minepalm.nations.server

import java.util.concurrent.CompletableFuture

interface NationServer {

    val name: String

    fun isOnline(): CompletableFuture<Boolean>

    fun isConnected(): Boolean

    fun send(obj: Any)

    fun <T> callback(obj: Any, returnType: Class<T>): CompletableFuture<T>

    fun setOnline(online: Boolean): CompletableFuture<Unit>
}