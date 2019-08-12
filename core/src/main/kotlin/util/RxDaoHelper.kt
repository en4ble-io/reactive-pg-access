package io.en4ble.pgaccess.util

import io.en4ble.pgaccess.DatabaseContext
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.exceptions.NoResultsException
import io.en4ble.pgaccess.util.DaoHelperCommon.addLimit
import io.en4ble.pgaccess.util.DaoHelperCommon.getQueryForLogging
import io.en4ble.pgaccess.util.DaoHelperCommon.getSortFields
import io.en4ble.pgaccess.util.JooqHelper.toUUIDList
import io.reactivex.Single
import io.vertx.reactivex.sqlclient.Row
import io.vertx.reactivex.sqlclient.RowSet
import io.vertx.reactivex.sqlclient.SqlClient
import io.vertx.reactivex.sqlclient.Transaction
import org.jooq.Query
import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.SelectOrderByStep
import org.jooq.Table
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.UUID

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object RxDaoHelper {
    private val LOG by lazy { LoggerFactory.getLogger(RxDaoHelper::class.java) }

    /**
     * Run a query using a connection from the pool.
     */
    fun query(query: Query, context: DatabaseContext): Single<RowSet> {
        return query(query, context.sqlClient, context)
    }

    /**
     * Run a query using a given connection.
     */
    fun query(query: Query, client: SqlClient, context: DatabaseContext): Single<RowSet> {
        return runQuery(query, client, context, false)
    }

    @Throws(NoResultsException::class)
    fun queryOne(query: Query, context: DatabaseContext): Single<Row> {
        return queryOne(query, context.sqlClient, context)
    }

    @Throws(NoResultsException::class)
    fun queryOne(query: Query, client: SqlClient, context: DatabaseContext): Single<Row> {
        return query(query, client, context)
            .map {
                if (it.size() == 0) {
                    throw NoResultsException(getQueryForLogging(query))
                }
                it.iterator().next()
            }
    }

    fun queryOptional(query: Query, context: DatabaseContext): Single<Optional<Row>> {
        return queryOptional(query, context.sqlClient, context)
    }

    fun queryOptional(query: Query, client: SqlClient, context: DatabaseContext): Single<Optional<Row>> {
        return query(query, client, context)
            .map {
                if (it.size() == 0) {
                    Optional.empty()
                } else {
                    Optional.of(it.iterator().next())
                }
            }
    }

    fun update(query: Query, context: DatabaseContext): Single<Int> {
        return update(query, context.sqlClient, context)
    }

    fun update(query: Query, client: SqlClient, context: DatabaseContext): Single<Int> {
        return runQuery(query, client, context, true)
            .map {
                val updateCount = it.rowCount()
                LOG.trace("updated: {} rows", updateCount)
                updateCount
            }
    }

    fun readUUIDs(query: Query, page: PagingDTO? = null, context: DatabaseContext): Single<List<UUID>> {
        return readUUIDs(query, page, context.sqlClient, context)
    }

    fun readUUIDs(
        query: Query,
        page: PagingDTO? = null,
        client: SqlClient,
        context: DatabaseContext
    ): Single<List<UUID>> {
        return if (page == null || query !is SelectLimitStep<*>) {
            query(query, client, context).map {
                toUUIDList(it.delegate)
            }
        } else {
            query(addLimit(query, page), client, context).map {
                toUUIDList(it.delegate)
            }
        }
    }

    fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        context: DatabaseContext
    ): Single<RowSet> {
        return read(query, table, page, context.sqlClient, context)
    }

    fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        context: DatabaseContext
    ): Single<RowSet> {
        if (page != null) {
            val order = page.orderBy
            if (order != null && query is SelectOrderByStep<*>) {
                val q = query.orderBy(getSortFields(table, order))
                return query(addLimit(q, page), client, context)
            }
        }
        return query(query, context)
    }

    private fun runQuery(query: Query, client: SqlClient, context: DatabaseContext, update: Boolean): Single<RowSet> {
        val sql = DaoHelperCommon.getSql(query, context)
        val inTx = if (client is Transaction) {
            "[Tx]"
        } else {
            "[NoTx]"
        }
        if (LOG.isTraceEnabled) {
            LOG.trace("{} about to run {}", inTx, sql)
        }
        return if (query.bindValues.isEmpty()) {
            if (LOG.isTraceEnabled) {
                LOG.trace("{} about to run {}: {}", inTx, if (update) "update" else "query", sql)
            }
            client.rxQuery(sql)
        } else {
            val params = JooqHelper.rxParams(query)
            if (LOG.isTraceEnabled) {
                LOG.trace(
                    "{} about to run {}: {}",
                    inTx,
                    if (update) "update" else "query",
                    getQueryForLogging(sql, params.delegate)
                )
            }
            client.rxPreparedQuery(sql, params)
        }.map {
            if (LOG.isTraceEnabled) {
                LOG.trace("{} query returned {} results", inTx, it.size())
            }
            it
        }
    }
}
