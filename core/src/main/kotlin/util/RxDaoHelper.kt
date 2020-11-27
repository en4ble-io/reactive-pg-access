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
import io.vertx.reactivex.sqlclient.SqlConnection
import org.jooq.*
import org.slf4j.LoggerFactory
import java.util.*

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object RxDaoHelper {
    private val LOG by lazy { LoggerFactory.getLogger(RxDaoHelper::class.java) }

    /**
     * Run a query using a connection from the pool.
     */
    fun query(query: Query, context: DatabaseContext): Single<RowSet<Row>> {
        return query(query, context.sqlClient, context)
    }

    /**
     * Run a query using a given connection.
     */
    fun query(query: Query, client: SqlClient, context: DatabaseContext): Single<RowSet<Row>> {
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
                    throw NoResultsException(getQueryForLogging(query, context))
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
                toUUIDList(it.delegate as io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>)
            }
        } else {
            query(addLimit(query, page), client, context).map {
                toUUIDList(it.delegate as io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>)
            }
        }
    }

    fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        context: DatabaseContext
    ): Single<RowSet<Row>> {
        return read(query, table, page, context.sqlClient, context)
    }

    fun <RECORD : Record> read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        context: DatabaseContext
    ): Single<RowSet<Row>> {
        if (page != null) {
            val order = page.orderBy
            if (order != null && query is SelectOrderByStep<*>) {
                val q = query.orderBy(getSortFields(table, order))
                return query(addLimit(q, page), client, context)
            }
        }
        return query(query, context)
    }

    private fun runQuery(
        query: Query,
        client: SqlClient,
        context: DatabaseContext,
        update: Boolean
    ): Single<RowSet<Row>> {
        val sql = DaoHelperCommon.getSql(query, context)
        // TODO: this is not exactly true, in vert.x 3.x it used to be a check on Transaction but it's not a superclass of SqlClient anymore.
        val inTx = if (client is SqlConnection) {
            "[Tx]"
        } else {
            "[NoTx]"
        }
        return if (query.bindValues.isEmpty()) {
            if (LOG.isTraceEnabled) {
                LOG.trace("{} about to run {}: {}", inTx, if (update) "update" else "query", sql)
            }
            client.query(sql).rxExecute()
        } else {
            if (context.config.preparedStatements) {
                val params = JooqHelper.rxParams(query)
                if (LOG.isTraceEnabled) {
                    LOG.trace(
                        "{} about to run {}: {}",
                        inTx,
                        if (update) "update" else "query",
                        getQueryForLogging(sql, params.delegate)
                    )
                }
                client.preparedQuery(sql).rxExecute(params)
            } else {
                client.query(sql).rxExecute()
            }
        }.doOnError {
            LOG.error("${it.message}\nsql: $sql", it)
            throw it
        }.map {
            if (LOG.isTraceEnabled) {
                LOG.trace("{} query returned {} results", inTx, it.size())
            }
            it
        }
    }
}
