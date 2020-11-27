@file:Suppress("unused")

package io.en4ble.pgaccess

import io.reactivex.Single
import io.vertx.kotlin.coroutines.await
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.pgclient.SslMode
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.sqlclient.SqlClient
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.conf.StatementType
import org.jooq.impl.CatalogImpl
import org.jooq.impl.DSL
import org.jooq.impl.SchemaImpl
import org.slf4j.LoggerFactory
import javax.validation.Validator

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DatabaseContext(
    val vertx: Vertx?,
    val config: DatabaseConfig,
    val validator: Validator? = null // optional validator, will be used before create
) {
    private val LOG by lazy { LoggerFactory.getLogger(DatabaseContext::class.java) }
    val dsl = DSL.using(SQLDialect.POSTGRES,
        if (config.preparedStatements) {
            Settings().withStatementType(StatementType.PREPARED_STATEMENT)
        } else {
            Settings().withStatementType(StatementType.STATIC_STATEMENT)
        }.withRenderMapping(RenderMapping()
            // replace hard coded _SCHEMA_ with configured schema
            // this is useful for shared/reused schema definitions
            .withSchemata(
                MappedSchema().withInput("_SCHEMA_")
                    .withOutput(config.schema)
            )
        )
    )
    val sqlClient: SqlClient

    constructor(vertx: Vertx, config: DatabaseConfig) : this(vertx, config, null)
    constructor(config: DatabaseConfig) : this(null, config)

    constructor(databaseContext: DatabaseContext) : this(
        databaseContext.vertx,
        databaseContext.config,
        databaseContext.validator
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

    private fun sqlClient(): SqlClient {
        return sqlClient
    }

    private fun initSqlClient(): SqlClient {
        val connectOptions = PgConnectOptions()
            .setHost(config.host)
            .setPort(config.port)
            // in order to connect to the database via a backend pooled connection as on DigitalOcean,
            // use the name of the pool instead of the database
            .setDatabase(config.database)
            .setUser(config.username)
            .setPassword(config.password)
            .setSslMode(
                if (config.ssl) {
                    SslMode.PREFER
                } else {
                    SslMode.ALLOW
                }
            )
            .setTrustAll(true)
        // Removes intervalStyle from default properties.
        // Setting this property causes connection attempts with PGBouncer pool to fail with "unsupported startup parameter: intervalStyle"
        // Since PgConnectOptions uses PostgreSQLs default value we can safely remove it.
        connectOptions.properties.remove("intervalStyle")
        val poolOptions = PoolOptions().setMaxSize(config.maxPoolSize)
        return if (vertx != null) {
            io.vertx.reactivex.pgclient.PgPool.pool(vertx, connectOptions, poolOptions)
        } else {
            io.vertx.reactivex.pgclient.PgPool.pool(connectOptions, poolOptions)
        }
    }

    fun rxBeginTx(): Single<Pair<io.vertx.reactivex.sqlclient.SqlConnection, io.vertx.reactivex.sqlclient.Transaction>> {
        return (sqlClient as io.vertx.reactivex.pgclient.PgPool).rxGetConnection()
            .flatMap { connection ->
                connection.rxBegin()
                    .map { connection to it }
            }
    }

    suspend fun beginTx(): Pair<SqlConnection, Transaction> {
        logBeginTx()
        val connection = (sqlClient.delegate as PgPool).connection.await()
        return connection to connection.begin().await()
    }

    suspend fun commitTx(connection: SqlConnection, transaction: Transaction) {
        logCommitTx()
        try {
            transaction.commit().await()
        } finally {
            connection.close().await()
        }
    }

    suspend fun rollbackTx(connection: SqlConnection, transaction: Transaction) {
        logRollbackTx()
        try {
            transaction.rollback().await()
        } finally {
            connection.close().await()
        }
    }

    fun commitTx(connection: io.vertx.reactivex.sqlclient.SqlConnection, transaction: io.vertx.reactivex.sqlclient.Transaction) {
        logCommitTx()
        try {
            transaction.commit()
        } finally {
            connection.close()
        }
    }

    fun rollbackTx(connection: io.vertx.reactivex.sqlclient.SqlConnection, transaction: io.vertx.reactivex.sqlclient.Transaction) {
        logRollbackTx()
        try {
            transaction.rollback()
        } finally {
            connection.close()
        }
    }

    private fun logBeginTx() {
        LOG.debug("Beginning new transaction")
    }

    private fun logCommitTx() {
        LOG.debug("Commiting transaction")
    }

    private fun logRollbackTx() {
        LOG.debug("Rolling back transaction")
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
//        val pool = sqlClient as io.vertx.reactivex.sqlclient.PgPool
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
//    fun getTx(): Transaction? {
//        val ctx = vertx.delegate.getOrCreateContext()
//        return ctx.get("tx")
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    fun getRxTx(): io.vertx.reactivex.sqlclient.Transaction? {
//        val ctx = vertx.getOrCreateContext()
//        return ctx.get("tx")
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    private fun getCtxTx(): Pair<Context, Transaction?> {
//        val ctx = vertx.delegate.getOrCreateContext()
//        val tx = ctx.get<Transaction>("tx")
//        return Pair(ctx, tx)
//    }
//
//    @Suppress("UsePropertyAccessSyntax")
//    private fun getRxCtxTx(): Pair<io.vertx.reactivex.core.Context, io.vertx.reactivex.sqlclient.Transaction?> {
//        val ctx = vertx.getOrCreateContext()
//        val tx = ctx.get<io.vertx.reactivex.sqlclient.Transaction>("tx")
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
