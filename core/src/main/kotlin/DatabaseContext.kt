@file:Suppress("unused")

package io.en4ble.pgaccess

import io.reactiverse.kotlin.pgclient.beginAwait
import io.reactiverse.pgclient.PgPool
import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.reactiverse.reactivex.pgclient.PgTransaction
import io.reactivex.Single
import io.vertx.reactivex.core.Vertx
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.Schema
import org.jooq.impl.CatalogImpl
import org.jooq.impl.DSL
import org.jooq.impl.SchemaImpl
import org.slf4j.LoggerFactory

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DatabaseContext(val vertx: Vertx?, val settings: DatabaseSettings, val schema: Schema?) {
    private val LOG by lazy { LoggerFactory.getLogger(DatabaseContext::class.java) }
    val dsl = DSL.using(SQLDialect.POSTGRES_10)!!
    val sqlClient: PgClient

    constructor(vertx: Vertx, settings: DatabaseSettings) : this(vertx, settings, null)
    constructor(settings: DatabaseSettings, schema: Schema) : this(null, settings, schema)
    constructor(settings: DatabaseSettings) : this(null, settings, null)

    constructor(databaseContext: DatabaseContext) : this(
        databaseContext.vertx,
        databaseContext.settings,
        databaseContext.schema
    )

    companion object {
        fun getSchemaImpl(schemaName: String, catalog: String?) =
            SchemaImpl(schemaName, if (catalog != null) CatalogImpl(catalog) else null)
    }

    val configuration: Configuration
        get() = dsl.configuration()

    init {
        this.sqlClient = initSqlClient()
    }

    private fun sqlClient(): PgClient {
        return sqlClient
    }

    private fun initSqlClient(): PgClient {
        val options = PgPoolOptions()
            .setHost(settings.host)
            .setPort(settings.port)
            .setDatabase(settings.database)
            .setUser(settings.username)
            .setPassword(settings.password)
            .setMaxSize(settings.maxPoolSize)
        return if (vertx != null) PgClient.pool(vertx, options) else PgClient.pool(options)
    }

    fun rxCreateTx(): Single<PgTransaction> {
        return (sqlClient as io.reactiverse.reactivex.pgclient.PgPool).rxBegin()
    }

    suspend fun createTx(): io.reactiverse.pgclient.PgTransaction {
        return (sqlClient.delegate as PgPool).beginAwait()
    }

    // --------------------------------------------------------------------------------------------------------
    // -- Transaction handler methods
    // NOTE: these methods use the vertx context to store the current transaction
    //  I'm not sure if this is working as expected, e.g if multiple REST calls access the database will
    //  they use different contexts and therefore also different transactions?
    //  In any case, anything put into the context stays there, even after the event completes
    //  (if the tx is not removed after a completed call it's still there on the next call)
    //
    //  An alternative solution could be to use Kotlins Coroutine Context, but it's not clear how to add values to it.

//    fun rxBeginTx(): Single<Boolean> {
//        val (ctx, tx) = getRxCtxTx()
//        if (tx != null) {
//            throw RuntimeException("transaction already exists")
//        }
//        val pool = sqlClient as io.reactiverse.reactivex.pgclient.PgPool
//        return pool.rxBegin().map {
//            it.abortHandler {
//                // TODO?
//                LOG.error("Transaction was aborted")
//            }
//
//            ctx.put("tx", it)
//            true
//        }
//    }
//
//    suspend fun beginTx() {
//        val (ctx, tx) = getCtxTx()
//        if (tx != null) {
//            throw RuntimeException("transaction already exists: $tx")
//        }
//        val pool = sqlClient.delegate as PgPool
//        val newTx = pool.beginAwait()
//        newTx.abortHandler {
//            // TODO?
//            LOG.error("Transaction was aborted")
//        }
//        ctx.put("tx", newTx)
//    }
//
//    suspend fun endTx() {
//        val (ctx, tx) = getCtxTx()
//        removeTx(tx, ctx)
//        tx?.commitAwait()
//    }
//
//    suspend fun commitTx() {
//        val (ctx, tx) = getCtxTx()
//        removeTx(tx, ctx)
//        try {
//            tx?.commitAwait()
//        } catch (e: Exception) {
//            LOG.error("unable to commit transaction: " + e.message)
//        }
//    }
//
//    fun rxCommitTx(): Completable {
//        val (ctx, tx) = getRxCtxTx()
//        removeTx(tx, ctx.delegate)
//        return if (tx != null) {
//            tx.rxCommit().onErrorComplete {
//                LOG.error("unable to commit transaction: " + it.message)
//                true
//            }
//        } else {
//            Completable.complete()
//        }
//    }
//
//    suspend fun rollbackTx() {
//        val (ctx, tx) = getCtxTx()
//        removeTx(tx, ctx)
//        try {
//            tx?.rollbackAwait()
//        } catch (e: Exception) {
//            LOG.warn("unable to rollback transaction: " + e.message)
//        }
//    }
//
//    fun rxRollbackTx(): Completable {
//        val (ctx, tx) = getRxCtxTx()
//        removeTx(tx, ctx.delegate)
//        return if (tx != null) {
//            tx.rxRollback().onErrorComplete {
//                LOG.error("unable to rollback transaction: " + it.message)
//                true
//            }
//        } else {
//            Completable.complete()
//        }
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    fun getTx(): PgTransaction? {
//        val ctx = vertx.delegate.getOrCreateContext()
//        return ctx.get("tx")
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    fun getRxTx(): io.reactiverse.reactivex.pgclient.PgTransaction? {
//        val ctx = vertx.getOrCreateContext()
//        return ctx.get("tx")
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    private fun getCtxTx(): Pair<Context, PgTransaction?> {
//        val ctx = vertx.delegate.getOrCreateContext()
//        val tx = ctx.get<PgTransaction>("tx")
//        return Pair(ctx, tx)
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    private fun getRxCtxTx(): Pair<io.vertx.reactivex.core.Context, io.reactiverse.reactivex.pgclient.PgTransaction?> {
//        val ctx = vertx.getOrCreateContext()
//        val tx = ctx.get<io.reactiverse.reactivex.pgclient.PgTransaction>("tx")
//        return Pair(ctx, tx)
//    }
//
//    private fun removeTx(tx: Any?, ctx: Context) {
//        if (tx == null) {
//            LOG.warn("transaction does not exist!")
//        } else {
//            ctx.remove("tx")
//        }
//    }
}
