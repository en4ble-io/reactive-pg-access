package io.en4ble.pgaccess.util

import io.en4ble.pgaccess.DatabaseContext
import io.en4ble.pgaccess.DatabaseContext.Companion.getSingleDatabaseContext
import io.en4ble.pgaccess.MultiDatabaseContext
import io.en4ble.pgaccess.SingleDatabaseContext
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.exceptions.NoResultsException
import io.en4ble.pgaccess.util.DaoHelperCommon.addLimit
import io.en4ble.pgaccess.util.DaoHelperCommon.getQueryForLogging
import io.en4ble.pgaccess.util.DaoHelperCommon.getSortFields
import io.en4ble.pgaccess.util.DaoHelperCommon.getSql
import io.en4ble.pgaccess.util.JooqHelper.toUUIDList
import io.vertx.kotlin.coroutines.await
import io.vertx.reactivex.sqlclient.SqlConnection
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import org.jooq.*
import org.slf4j.LoggerFactory
import java.util.*

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object DaoHelper {
    private val LOG by lazy { LoggerFactory.getLogger(DaoHelper::class.java) }

    /**
     * Run a query using a connection from the pool.
     */
    suspend fun query(query: Query, context: DatabaseContext): RowSet<Row> {
        val sc = getSingleDatabaseContext(context)
        return query(query, sc.sqlClient().delegate, sc)
    }

    /**
     * Run a query using a given connection.
     */
    suspend fun query(query: Query, client: SqlClient, context: SingleDatabaseContext): RowSet<Row> {
        return runQuery(query, client, context, false)
    }

    /**
     * Run a query using a connection from the pool, returning a single Row.
     * @throws NoResultsException if no matching row was found.
     */
    @Throws(NoResultsException::class)
    suspend fun queryOne(query: Query, context: DatabaseContext): Row {
        val sc = getSingleDatabaseContext(context)
        return queryOne(query, sc.sqlClient().delegate, sc)
    }

    /**
     * Run a query using the given connection, returning a single Row.
     * @throws NoResultsException if no matching row was found.
     */
    @Throws(NoResultsException::class)
    suspend fun queryOne(query: Query, client: SqlClient, context: SingleDatabaseContext): Row {
        val res = query(query, client, context)
        if (res.size() == 0) {
            throw NoResultsException(getQueryForLogging(query, context.config()))
        }
        return res.first()
    }

    /**
     * Run a query using a connection from the pool, returning a single Row or an empty Optional
     */
    suspend fun queryOptional(query: Query, context: DatabaseContext): Optional<Row> {
        val sc = getSingleDatabaseContext(context)
        return queryOptional(query, sc.sqlClient().delegate, sc)
    }

    suspend fun queryOptional(query: Query, client: SqlClient, context: SingleDatabaseContext): Optional<Row> {
        val res = query(query, client, context)
        return if (res.size() == 0) {
            Optional.empty()
        } else {
            Optional.of(res.first())
        }
    }

    suspend fun update(query: Query, context: DatabaseContext): Int {
        val sc = getSingleDatabaseContext(context)
        return update(query, sc.sqlClient().delegate, sc)
    }

    suspend fun update(query: Query, client: SqlClient, context: SingleDatabaseContext): Int {
        val res = runQuery(query, client, context, true)
        val updateCount = res.rowCount()
        LOG.trace("updated: {} rows", updateCount)
        return updateCount
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null, context: DatabaseContext): List<UUID> {
        val sc = getSingleDatabaseContext(context)
        return readUUIDs(query, page, sc.sqlClient().delegate, sc)
    }

    suspend fun readUUIDs(
        query: Query,
        page: PagingDTO? = null,
        client: SqlClient,
        context: SingleDatabaseContext
    ): List<UUID> {
        return if (page == null || query !is SelectLimitStep<*>) {
            toUUIDList(query(query, client, context))
        } else {
            toUUIDList(query(addLimit(query, page), client, context))
        }
    }

    suspend fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        context: DatabaseContext
    ): RowSet<Row> {
        val c = if (context is MultiDatabaseContext) {
            context.current()
        } else {
            context
        }
        return read(query, table, page, c.sqlClient().delegate, c as SingleDatabaseContext)
    }

    suspend fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        context: SingleDatabaseContext
    ): RowSet<Row> {
        if (page != null) {
            val order = page.orderBy
            if (order != null && query is SelectOrderByStep<*>) {
                val q = query.orderBy(getSortFields(table, order))
                return query(addLimit(q, page), client, context)
            }
        }
        return query(query, context)
    }

    private suspend fun runQuery(
        query: Query,
        client: SqlClient,
        context: SingleDatabaseContext,
        update: Boolean
    ): RowSet<Row> {
        val sql = getSql(query, context.config())
        // TODO: this is not exactly true, in vert.x 3.x it used to be a check on Transaction but it's not a superclass of SqlClient anymore.
        val inTx = if (client is SqlConnection) {
            "[Tx]"
        } else {
            "[NoTx]"
        }
        try {
            val result = if (query.bindValues.isEmpty()) {
                if (LOG.isTraceEnabled) {
                    LOG.trace("{} about to run {}: {}", inTx, if (update) "update" else "query", sql)
                }
                client.query(sql).execute().await()
            } else {
                if (context.config().preparedStatements) {
                    val params = JooqHelper.rxParams(query)
                    if (LOG.isTraceEnabled) {
                        LOG.trace(
                            "{} about to run {}: {}",
                            inTx,
                            if (update) "update" else "query",
                            getQueryForLogging(sql, params.delegate)
                        )
                    }
                    client.preparedQuery(sql).execute(params.delegate).await()
                } else {
                    if (LOG.isTraceEnabled) {
                        LOG.trace(
                            "{} about to run {}: {}",
                            inTx,
                            if (update) "update" else "query", sql
                        )
                    }
                    client.query(sql).execute().await()
                }
            }
            if (LOG.isTraceEnabled) {
                LOG.trace("{} query returned {} results", inTx, result.size())
            }
            return result
        } catch (e: Exception) {
            LOG.error("${e.message}\nsql: $sql", e)
            throw e
        }
    }

}
