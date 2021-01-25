package io.en4ble.pgaccess

import io.vertx.reactivex.core.Vertx

class DynamicDatabaseContext(private val vertx: Vertx, private val contextMap: Map<String, DatabaseContext>) {
    fun current(): DatabaseContext {
        // TODO: get client ID from coroutine context
        TODO("return db context for current client")
    }
}
