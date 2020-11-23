package io.en4ble.pgaccess.util

import io.en4ble.pgaccess.DatabaseContext
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.exceptions.NoResultsException
import io.en4ble.pgaccess.util.DaoHelperCommon.addLimit
import io.en4ble.pgaccess.util.DaoHelperCommon.getQueryForLogging
import io.en4ble.pgaccess.util.DaoHelperCommon.getSortFields
import io.en4ble.pgaccess.util.DaoHelperCommon.getSql
import io.en4ble.pgaccess.util.JooqHelper.toUUIDList
import io.vertx.kotlin.coroutines.await
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
        return query(query, context.sqlClient.delegate, context)
    }

    /**
     * Run a query using a given connection.
     */
    suspend fun query(query: Query, client: SqlClient, context: DatabaseContext): RowSet<Row> {
        return runQuery(query, client, context, false)
    }

    /**
     * Run a query using a connection from the pool, returning a single Row.
     * @throws NoResultsException if no matching row was found.
     */
    @Throws(NoResultsException::class)
    suspend fun queryOne(query: Query, context: DatabaseContext): Row {
        return queryOne(query, context.sqlClient.delegate, context)
    }

    /**
     * Run a query using the given connection, returning a single Row.
     * @throws NoResultsException if no matching row was found.
     */
    @Throws(NoResultsException::class)
    suspend fun queryOne(query: Query, client: SqlClient, context: DatabaseContext): Row {
        val res = query(query, client, context)
        if (res.size() == 0) {
            throw NoResultsException(getQueryForLogging(query, context))
        }
        return res.first()
    }

    /**
     * Run a query using a connection from the pool, returning a single Row or an empty Optional
     */
    suspend fun queryOptional(query: Query, context: DatabaseContext): Optional<Row> {
        return queryOptional(query, context.sqlClient.delegate, context)
    }

    suspend fun queryOptional(query: Query, client: SqlClient, context: DatabaseContext): Optional<Row> {
        val res = query(query, client, context)
        return if (res.size() == 0) {
            Optional.empty()
        } else {
            Optional.of(res.first())
        }
    }

    suspend fun update(query: Query, context: DatabaseContext): Int {
        return update(query, context.sqlClient.delegate, context)
    }

    suspend fun update(query: Query, client: SqlClient, context: DatabaseContext): Int {
        val res = runQuery(query, client, context, true)
        val updateCount = res.rowCount()
        LOG.trace("updated: {} rows", updateCount)
        return updateCount
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null, context: DatabaseContext): List<UUID> {
        return readUUIDs(query, page, context.sqlClient.delegate, context)
    }

    suspend fun readUUIDs(
        query: Query,
        page: PagingDTO? = null,
        client: SqlClient,
        context: DatabaseContext
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
        return read(query, table, page, context.sqlClient.delegate, context)
    }

    suspend fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        context: DatabaseContext
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
        context: DatabaseContext,
        update: Boolean
    ): RowSet<Row> {
        val sql = getSql(query, context)
        val inTx = if (context.inTransaction) {
            "[Tx]"
        } else {
            "[NoTx]"
        }
        if (LOG.isTraceEnabled) {
            LOG.trace("{} about to run {}", inTx, sql)
        }
        val result = if (query.bindValues.isEmpty()) {
            if (LOG.isTraceEnabled) {
                LOG.trace("{} about to run {}: {}", inTx, if (update) "update" else "query", sql)
            }
            client.query(sql).execute().await()
        } else {
            val params = JooqHelper.params(query)
            if (LOG.isTraceEnabled) {
                LOG.trace(
                    "{} about to run {}: {}",
                    inTx,
                    if (update) "update" else "query",
                    getQueryForLogging(sql, params)
                )
            }
            client.preparedQuery(sql).execute(params).await()
        }
        if (LOG.isTraceEnabled) {
            LOG.trace("{} query returned {} results", inTx, result.size())
        }
        return result
    }

}
