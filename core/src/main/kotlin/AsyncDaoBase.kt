package io.en4ble.pgaccess

import io.en4ble.pgaccess.DatabaseContext.Companion.getSingleDatabaseContext
import io.en4ble.pgaccess.DatabaseContext.Companion.toSingleDatabaseContext
import io.en4ble.pgaccess.dto.OrderDTO
import io.en4ble.pgaccess.dto.PageDTO
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.dto.PointDTO
import io.en4ble.pgaccess.enumerations.SortDirection
import io.en4ble.pgaccess.enumerations.TypedEnum
import io.en4ble.pgaccess.util.DaoHelper
import io.en4ble.pgaccess.util.DaoHelperCommon
import io.en4ble.pgaccess.util.JooqHelper
import io.en4ble.pgaccess.util.RxDaoHelper
import io.reactivex.Single
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.reactivex.sqlclient.SqlClient
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import org.jooq.*
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
abstract class AsyncDaoBase<RECORD : Record, DTO>
protected constructor(
    @Suppress("CanBeParameter") protected val context: DatabaseContext,
    protected val table: Table<RECORD>,
    private val dtoType: Class<DTO>
) {
    protected val LOG by lazy { LoggerFactory.getLogger(AsyncDaoBase::class.java) }

    protected val dsl: DSLContext = context.dsl()

    protected open fun <ID> primaryKeyField(): TableField<RECORD, ID>? {
        val primaryKeyFields = table().primaryKey?.fields
        if (primaryKeyFields.isNullOrEmpty()) {
            return null
        }
        val firstIdField = primaryKeyFields[0]
        return if (firstIdField == null) null else firstIdField as TableField<RECORD, ID>
    }

    protected open fun table(): Table<RECORD> {
        return table
    }

    // -- query optional
    suspend fun queryOptional(query: Query): Optional<Row> {
        return DaoHelper.queryOptional(query, context)
    }

    suspend fun queryOptional(query: Query, client: io.vertx.sqlclient.SqlClient): Optional<Row> {
        return DaoHelper.queryOptional(query, client, toSingleDatabaseContext(context))
    }

    suspend fun queryOptional(query: Query, context: DatabaseContext): Optional<Row> {
        return DaoHelper.queryOptional(query, context)
    }

    suspend fun queryOptional(
        query: Query,
        client: io.vertx.sqlclient.SqlClient,
        context: SingleDatabaseContext
    ): Optional<Row> {
        return DaoHelper.queryOptional(query, client, context)
    }

    fun rxQueryOptional(query: Query, clientId: String? = null): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.queryOptional(query, sc)
    }

    fun rxQueryOptional(
        query: Query,
        client: SqlClient,
        clientId: String? = null
    ): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.queryOptional(query, client, sc)
    }

    fun rxQueryOptional(
        query: Query,
        context: DatabaseContext
    ): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOptional(query, sc)
    }

    fun rxQueryOptional(
        query: Query, client: SqlClient, context: DatabaseContext
    ): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOptional(query, client, sc)
    }

    // -- query

    suspend fun query(condition: Condition, context: DatabaseContext): RowSet<Row> {
        return DaoHelper.query(getQuery(condition, table(), dsl), context)
    }

    suspend fun query(
        condition: Condition,
        client: io.vertx.sqlclient.SqlClient,
        context: SingleDatabaseContext
    ): RowSet<Row> {
        return DaoHelper.query(getQuery(condition, table(), dsl), client, context)
    }

    suspend fun query(query: Query, context: DatabaseContext): RowSet<Row> {
        return DaoHelper.query(query, context)
    }

    suspend fun query(query: Query, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): RowSet<Row> {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.query(query, client, sc)
    }

    fun rxQuery(
        condition: Condition, client: SqlClient, context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.query(getQuery(condition, table(), dsl), client, sc)
    }

    fun rxQuery(
        condition: Condition,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.query(getQuery(condition, table(), dsl), sc)
    }

    fun rxQuery(
        query: Query,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.query(query, sc)
    }

    fun rxQuery(
        query: Query,
        client: SqlClient,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.query(query, client, sc)
    }

    suspend fun queryOne(condition: Condition, context: DatabaseContext): Row {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.queryOne(getQuery(condition, table(), dsl), sc)
    }

    suspend fun queryOne(condition: Condition, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): Row {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.queryOne(getQuery(condition, table(), dsl), client, sc)
    }

    fun rxQueryOne(
        condition: Condition,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOne(getQuery(condition, table(), dsl), sc)
    }

    fun rxQueryOne(
        condition: Condition, client: SqlClient, context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOne(getQuery(condition, table(), dsl), client, sc)
    }

    suspend fun queryOne(query: Query, context: DatabaseContext): Row {
        return DaoHelper.queryOne(query, context)
    }

    suspend fun queryOne(query: Query, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): Row {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.queryOne(query, client, sc)
    }

    suspend fun query(query: Query): RowSet<Row> {
        return DaoHelper.query(query, context)
    }

    suspend fun query(query: Query, client: io.vertx.sqlclient.SqlClient): RowSet<Row> {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.query(query, client, sc)
    }

    suspend fun queryOne(query: Query): Row {
        return DaoHelper.queryOne(query, context)
    }

    suspend fun queryOne(query: Query, client: io.vertx.sqlclient.SqlClient): Row {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.queryOne(query, client, sc)
    }

    fun rxQueryOne(query: Query, context: DatabaseContext): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOne(query, sc)
    }

    fun rxQueryOne(
        query: Query,
        client: SqlClient,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = toSingleDatabaseContext(context)
        return RxDaoHelper.queryOne(query, client, sc)
    }

    fun rxQuery(
        query: Query,
        clientId: String? = null
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.query(query, sc)
    }

    fun rxQuery(
        query: Query,
        client: SqlClient,
        clientId: String? = null
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.query(query, client, sc)
    }

    fun rxQueryOne(query: Query, clientId: String? = null): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.queryOne(query, sc)
    }

    fun rxQueryOne(
        query: Query,
        client: SqlClient,
        clientId: String? = null
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.queryOne(query, client, sc)
    }

    // read uuids
    suspend fun readUUIDs(
        idField: TableField<*, *>, condition: Condition
    ): List<UUID> {
        return readUUIDs(idField, condition, null, context.sqlClient().delegate)
    }

    suspend fun readUUIDs(
        idField: TableField<*, *>, selectFrom: Table<*>, condition: Condition, page: PagingDTO? = null
    ): List<UUID> {
        return readUUIDs(idField, selectFrom, condition, page, context.sqlClient().delegate)
    }

    suspend fun readUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null, client: io.vertx.sqlclient.SqlClient
    ): List<UUID> {
        return readUUIDs(idField, idField.table, condition, page, client)
    }

    suspend fun readUUIDs(
        idField: TableField<*, *>,
        selectFrom: Table<*>,
        condition: Condition, page: PagingDTO? = null, client: io.vertx.sqlclient.SqlClient
    ): List<UUID> {
        val sc = toSingleDatabaseContext(context)
        val query = dsl.select(idField).from(selectFrom).where(condition)
        return DaoHelper.readUUIDs(query, page, client, sc)
    }

    fun rxReadUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null, clientId: String? = null
    ): Single<List<UUID>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return rxReadUUIDs(idField, condition, page, sc.sqlClient())
    }

    fun rxReadUUIDs(
        idField: TableField<*, *>,
        condition: Condition,
        page: PagingDTO? = null,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<UUID>> {
        val sc = getSingleDatabaseContext(context, clientId)
        val query = dsl.select(idField).from(idField.table).where(condition)
        return RxDaoHelper.readUUIDs(query, page, client, sc)
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null): List<UUID> {
        return DaoHelper.readUUIDs(query, page, context)
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null, client: SqlClient): List<UUID> {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.readUUIDs(query, page, client.delegate, sc)
    }

    fun rxReadUUIDs(query: Query, page: PagingDTO? = null, clientId: String? = null): Single<List<UUID>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.readUUIDs(query, page, sc)
    }

    fun rxReadUUIDs(
        query: Query,
        page: PagingDTO? = null,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<UUID>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.readUUIDs(query, page, client, sc)
    }

    fun <ID> rxReadPage(page: PageDTO<ID>, clientId: String? = null): Single<List<DTO>> {
        return rxReadPage(null, page, clientId)
    }

    fun <ID> rxReadPage(condition: Condition? = null, page: PageDTO<ID>, clientId: String? = null): Single<List<DTO>> {
        return rxReadPage(
            condition,
            page.baseId,
            getBaseValues(page.orderByList, page.baseValues),
            page.orderByList,
            page.size,
            clientId
        )
    }

    fun <ID> rxReadPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: OrderDTO,
        pageSize: Int,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxReadPage(condition, baseId, baseValues, listOf(orderBy), pageSize, clientId)
    }

    fun <ID, ORDER_BY> rxReadPageCustomOrder(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>?,
        orderBy: List<SortField<ORDER_BY>>,
        pageSize: Int,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxReadPage<ID>(
            getReadPageQueryOrderBy(condition, orderBy, baseId),
            baseId,
            baseValues ?: emptyArray(),
            pageSize,
            clientId
        )
    }

    fun <ID> rxReadPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: List<OrderDTO>,
        pageSize: Int,
        clientId: String? = null
    ): Single<List<DTO>> {
        checkValuesAndOrderBy(baseId, baseValues, orderBy)
        return rxReadPage<ID>(getReadPageQuery(condition, orderBy, baseId), baseId, baseValues, pageSize, clientId)
    }

    suspend fun <ID> readPage(page: PageDTO<ID>): List<DTO> {
        return readPage(null, page)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <ID> rxReadPage(
        baseQuery: SelectSeekStepN<Record>,
        page: PageDTO<ID>,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxReadPage(baseQuery, page.baseId, page.baseValues?.toTypedArray(), page.size, clientId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <ID> rxReadPage(
        baseQuery: SelectSeekStepN<Record>,
        baseId: ID?,
        baseValues: Array<Any?>?,
        pageSize: Int,
        clientId: String? = null
    ): Single<List<DTO>> {
        return if (baseId != null) {
            val array = arrayOf(*baseValues!!, baseId)
            val query = baseQuery.seek(*array)
            rxQuery(query.limit(pageSize), clientId)
        } else {
            rxQuery(baseQuery.limit(pageSize), clientId)
        }.map { map(it.delegate as RowSet<Row>) }
    }

    suspend fun <ID> readPage(condition: Condition? = null, page: PageDTO<ID>): List<DTO> {
        return readPage(
            condition,
            page.baseId,
            getBaseValues(page.orderByList, page.baseValues),
            page.orderByList,
            page.size
        )
    }

    suspend fun <ID> readPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: OrderDTO,
        pageSize: Int
    ): List<DTO> {
        return readPage(condition, baseId, baseValues, listOf(orderBy), pageSize)
    }

//    suspend fun <ID> readPage(
//        condition: Condition? = null,
//        baseId: ID?,
//        baseValues: Array<Any?>,
//        orderBy: List<OrderDTO>,
//        pageSize: Int
//    ): List<DTO> {
//        return readPage(condition, baseId, baseValue, orderBy, pageSize)
//    }


    /**
     * Reads a page using "keyset pagination" via jOOQs seek method.
     * @param baseId The id of the last entry of the last page.
     * @param baseValues The value(s) of the last entry(ies) of the last page that is used for sorting. Must match the type of the field in the OrderDTO.
     * @param orderBy Information on how to sort the list.
     * @param pageSize The maximum number of results to return.
     */
    suspend fun <ID> readPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): List<DTO> {
        checkValuesAndOrderBy(baseId, baseValues, orderBy)
        return readPage<ID>(getReadPageQuery(condition, orderBy, baseId), baseId, baseValues, pageSize)
    }

    suspend fun <ID> readPage(
        selectFrom: Table<*>,
        condition: Condition,
        page: PageDTO<ID>
    ): List<DTO> {
        val baseId = page.baseId
        if (baseId != null) {
            checkValuesAndOrderBy(baseId, page.baseValues?.toTypedArray(), page.orderByList)
        }
        return readPage(
            selectFrom,
            condition,
            baseId,
            page.baseValues?.toTypedArray() ?: emptyArray(),
            page.orderByList,
            page.size
        )
    }

    suspend fun <ID> readPage(
        selectFrom: Table<*>,
        condition: Condition,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): List<DTO> {
        checkValuesAndOrderBy(baseId, baseValues, orderBy)
        return readPage(
            getReadPageQueryOrderBy(selectFrom, condition, getOrderByList(orderBy), baseId),
            baseId,
            baseValues,
            pageSize
        )
    }

    suspend fun <ID, ORDER_BY, SELECT : Table<RECORD>> readPageCustomOrder(
        selectFrom: SELECT,
        condition: Condition?,
        baseId: ID?,
        baseValues: Array<Any?>?,
        orderBy: List<SortField<ORDER_BY>>,
        pageSize: Int
    ): List<DTO> {
        return readPage(
            getReadPageQueryOrderBy(selectFrom, condition, orderBy, baseId),
            baseId,
            baseValues ?: emptyArray(),
            pageSize
        )
    }

    suspend fun <ID, ORDER_BY> readPageCustomOrder(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>?,
        orderBy: List<SortField<ORDER_BY>>,
        pageSize: Int
    ): List<DTO> {
        return readPage(
            getReadPageQueryOrderBy(condition, orderBy, baseId),
            baseId,
            baseValues ?: emptyArray(),
            pageSize
        )
    }

    private suspend fun <ID> readPage(
        baseQuery: SelectSeekStepN<Record>,
        baseId: ID?,
        baseValues: Array<Any?>,
        pageSize: Int
    ): List<DTO> {
        val res = if (baseId != null) {
            val array = arrayOf(*baseValues, baseId)
            val query = baseQuery.seek(*array)
            query(query.limit(pageSize))
        } else {
            query(baseQuery.limit(pageSize))
        }
        return map(res)
    }


    protected fun checkValuesAndOrderBy(baseId: Any?, baseValues: Array<Any?>?, orderBy: List<OrderDTO>?) {
        if (baseValues == null || orderBy == null) {
            if (baseId != null) {
                throw ValidationException("baseValues and orderBy must not be empty if baseId is defined.")
            }
            return
        }
        val baseValuesSize = baseValues.size
        val orderBySize = orderBy.size
        if (baseId != null && baseValuesSize != orderBySize) {
            throw ValidationException("baseValues size ($baseValuesSize) does not match orderBy size ($orderBySize)")
        }
        // baseValue type must match type of orderBy field
        for (i in baseValues.indices) {
            val baseValue = baseValues[i] ?: continue
            val orderField = orderBy[i].field
            getDbField(orderField).type.let { dbFieldType ->
                if (!dbFieldType.isInstance(baseValue)) {
                    throw RuntimeException("order field has type ${baseValue.javaClass} but must be $dbFieldType")
                }
            }
        }
    }

    /**
     * Reads a page using "offset pagination"
     * @param orderBy Information on how to sort the list.
     * @param firstPage The first page to query.
     * @param pageSize The maximum number of results to return.
     */
    suspend fun readPage(
        condition: Condition,
        orderBy: List<OrderDTO>,
        firstPage: Int,
        pageSize: Int
    ): List<DTO> {
        val offset = pageSize * firstPage
        val readPageQuery = getReadPageQuery(condition, offset, orderBy)
        val res = query(readPageQuery.limit(pageSize))
        return map(res)
    }

    protected fun getOrderByList(orderBy: List<OrderDTO>) =
        orderBy.map { order -> getOrderBy(order) }.toMutableList()

    protected fun getOrderBy(order: OrderDTO): SortField<out Any> {
        val sortDirection = order.direction
        val orderField = getDbField(order.field)
        return if (sortDirection === SortDirection.ASC)
            orderField.asc()
        else
            orderField.desc()
    }

    protected fun getReadPageQuery(
        condition: Condition,
        offset: Int,
        orderBy: List<OrderDTO>
    ): SelectLimitAfterOffsetStep<Record> {
        val select = dsl.select().from(table()).where(condition)
        return select.orderBy(getOrderByList(orderBy)).offset(offset)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <ID> getReadPageQuery(
        condition: Condition?,
        orderBy: List<OrderDTO>,
        baseId: ID?
    ): SelectSeekStepN<Record> {
        val offset = getOrderByList(orderBy)
        return getReadPageQueryOrderBy(condition, offset, baseId)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <ID, ORDER_BY> getReadPageQueryOrderBy(
        condition: Condition?,
        orderBy: List<SortField<out ORDER_BY>>,
        baseId: ID?
    ): SelectSeekStepN<Record> {
        return getReadPageQueryOrderBy(table(), condition, orderBy, baseId)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <ID, ORDER_BY> getReadPageQueryOrderBy(
        selectFrom: Table<*>,
        condition: Condition?,
        orderBy: List<SortField<out ORDER_BY>>,
        baseId: ID?
    ): SelectSeekStepN<Record> {
        val idField = primaryKeyField<ID>()
            ?: throw RuntimeException(
                "The read page query requires a primary key field in the table/view ${table().name}.\n" +
                    " Either add a primary key to the table or override the function primaryKeyField() in ${this.javaClass.name} to return the desired primary key field of the view\n" +
                    " example:\n" +
                    "    override fun <ID> primaryKeyField(): TableField<ProfileListRecord, ID>? {\n" +
                    "        return ProfileList.PROFILE_LIST.ID as TableField<ProfileListRecord, ID>\n" +
                    "    }"
            )

        val baseSelect = dsl.select().from(selectFrom)
        val select =
            if (condition != null) {
                baseSelect.where(condition)
            } else {
                baseSelect
            }

        if (baseId != null && !idField.type.isInstance(baseId)) {
            throw ValidationException("Given base id does not match type of database id (${idField.type}")
        }

        return select.orderBy(*orderBy.toTypedArray(), idField.desc())
    }

    /**
     * Gets the database field that matches the given dto field.
     */
    abstract fun getDbField(dtoField: String): Field<*>

    // ---------- helper methods using generated mappers

    suspend fun readAll(): List<DTO> {
        return read(dsl.select().from(table), table)
    }

    suspend fun readAll(table: Table<RECORD>): List<DTO> {
        return read(dsl.select().from(table), table)
    }

    suspend fun readAll(client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(dsl.select().from(table), table, client)
    }

    suspend fun readAll(table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(dsl.select().from(table), table, client)
    }

    fun rxReadAll(clientId: String? = null): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table, clientId)
    }

    fun rxReadAll(table: Table<RECORD>, clientId: String? = null): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table, clientId)
    }

    fun rxReadAll(client: SqlClient, clientId: String? = null): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table, client, clientId)
    }

    fun rxReadAll(table: Table<RECORD>? = null, client: SqlClient, clientId: String? = null): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table ?: this.table, client, clientId)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>): List<DTO> {
        return read(getQuery(condition, table, dsl), table)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(getQuery(condition, table, dsl), table, client)
    }

    fun rxRead(condition: Condition, table: Table<RECORD>, clientId: String? = null): Single<List<DTO>> {
        return rxRead(getQuery(condition, table, dsl), table, clientId)
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(getQuery(condition, table, dsl), table, client, clientId)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>, page: PagingDTO?): List<DTO> {
        return read(getQuery(condition, table, dsl), table, page)
    }

    suspend fun read(
        condition: Condition,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: io.vertx.sqlclient.SqlClient
    ): List<DTO> {
        return read(getQuery(condition, table, dsl), table, page, client)
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        page: PagingDTO?,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(getQuery(condition, table, dsl), table, page, clientId)
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(getQuery(condition, table, dsl), table, page, client, clientId)
    }

    suspend fun read(condition: Condition, orderBy: List<OrderDTO>): List<DTO> {
        return read(condition, table(), PagingDTO(orderBy))
    }

    suspend fun read(condition: Condition, orderBy: List<OrderDTO>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(condition, table(), PagingDTO(orderBy), client)
    }

    fun rxRead(condition: Condition, orderBy: List<OrderDTO>, clientId: String? = null): Single<List<DTO>> {
        return rxRead(condition, table(), PagingDTO(orderBy), clientId)
    }

    fun rxRead(
        condition: Condition,
        orderBy: List<OrderDTO>,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(condition, table(), PagingDTO(orderBy), client, clientId)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>, orderBy: List<OrderDTO>): List<DTO> {
        return read(condition, table, PagingDTO(orderBy))
    }

    suspend fun read(
        condition: Condition,
        table: Table<RECORD>,
        orderBy: List<OrderDTO>,
        client: io.vertx.sqlclient.SqlClient
    ): List<DTO> {
        return read(condition, table, PagingDTO(orderBy), client)
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        orderBy: List<OrderDTO>,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(condition, table, PagingDTO(orderBy), clientId)
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        orderBy: List<OrderDTO>,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<DTO>> {
        return rxRead(condition, table, PagingDTO(orderBy), client, clientId)
    }

    suspend fun read(query: Query, table: Table<RECORD>): List<DTO> {
        return read(query, table, null)
    }

    suspend fun read(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(query, table, null, client)
    }

    fun rxRead(query: Query, table: Table<RECORD>, clientId: String? = null): Single<List<DTO>> {
        return rxRead(query, table, null, clientId)
    }

    fun rxRead(query: Query, table: Table<RECORD>, client: SqlClient, clientId: String? = null): Single<List<DTO>> {
        return rxRead(query, table, null, client, clientId)
    }

    suspend fun read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?
    ): List<DTO> {
        return DaoHelper.read(query, table, page, context).map { map(it, table) }
    }

    suspend fun read(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: io.vertx.sqlclient.SqlClient
    ): List<DTO> {
        val sc = toSingleDatabaseContext(context)
        return DaoHelper.read(query, table, page, client, sc).map { map(it, table) }
    }

    fun rxRead(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        clientId: String? = null
    ): Single<List<DTO>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.read(query, table, page, sc).map { map(it.delegate as RowSet<Row>, table) }
    }

    fun rxRead(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient,
        clientId: String? = null
    ): Single<List<DTO>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return RxDaoHelper.read(query, table, page, client, sc).map { map(it.delegate as RowSet<Row>, table) }
    }

    suspend fun readOne(condition: Condition, table: Table<RECORD>): DTO {
        return readOne(getQuery(condition, table, dsl), table)
    }

    suspend fun readOne(condition: Condition, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): DTO {
        return readOne(getQuery(condition, table, dsl), table, client)
    }

    fun rxReadOne(condition: Condition, table: Table<RECORD>, clientId: String? = null): Single<DTO> {
        return rxReadOne(getQuery(condition, table, dsl), table, clientId)
    }

    fun rxReadOne(
        condition: Condition,
        table: Table<RECORD>,
        client: SqlClient,
        clientId: String? = null
    ): Single<DTO> {
        return rxReadOne(getQuery(condition, table, dsl), table, client, clientId)
    }

    suspend fun readOne(query: Query, table: Table<RECORD>): DTO {
        return map(queryOne(query), table)
    }

    suspend fun readOne(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): DTO {
        return map(queryOne(query, client), table)
    }

    fun rxReadOne(query: Query, table: Table<RECORD>, clientId: String? = null): Single<DTO> {
        return rxQueryOne(query, clientId).map {
            map(it.delegate, table)
        }
    }

    fun rxReadOne(query: Query, table: Table<RECORD>, client: SqlClient, clientId: String? = null): Single<DTO> {
        return rxQueryOne(query, client, clientId).map {
            map(it.delegate, table)
        }
    }

    suspend fun readOptional(condition: Condition, table: Table<RECORD>): Optional<DTO> {
        return readOptional(getQuery(condition, table, dsl), table)
    }

    suspend fun readOptional(
        condition: Condition,
        table: Table<RECORD>,
        client: io.vertx.sqlclient.SqlClient
    ): Optional<DTO> {
        return readOptional(getQuery(condition, table, dsl), table, client)
    }

    fun rxReadOptional(condition: Condition, table: Table<RECORD>, clientId: String? = null): Single<Optional<DTO>> {
        return rxReadOptional(getQuery(condition, table, dsl), table, clientId)
    }

    fun rxReadOptional(
        condition: Condition,
        table: Table<RECORD>,
        client: SqlClient,
        clientId: String? = null
    ): Single<Optional<DTO>> {
        return rxReadOptional(getQuery(condition, table, dsl), table, client, clientId)
    }

    suspend fun readOptional(query: Query, table: Table<RECORD>): Optional<DTO> {
        return readOptional(query, table, context.sqlClient().delegate)
    }

    suspend fun readOptional(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): Optional<DTO> {
        val res = queryOptional(query, client)
        return if (res.isPresent) {
            Optional.of(map(res.get(), table)!!)
        } else {
            Optional.empty()
        }
    }

    fun rxReadOptional(query: Query, table: Table<RECORD>, clientId: String? = null): Single<Optional<DTO>> {
        val sc = getSingleDatabaseContext(context, clientId)
        return rxReadOptional(query, table, sc.sqlClient(), clientId)
    }

    fun rxReadOptional(
        query: Query,
        table: Table<RECORD>,
        client: SqlClient,
        clientId: String? = null
    ): Single<Optional<DTO>> {
        return rxQueryOptional(query, client, clientId).map {
            if (it.isPresent) {
                Optional.of(map(it.get().delegate, table)!!)
            } else {
                Optional.empty()
            }
        }
    }

    suspend fun readCount(countQuery: Query): Int {
        return queryOne(countQuery).getInteger(0)
    }

    suspend fun readCount(countQuery: Query, client: io.vertx.sqlclient.SqlClient): Int {
        return queryOne(countQuery, client).getInteger(0)
    }

    fun rxReadCount(countQuery: Query, clientId: String? = null): Single<Int> {
        return rxQueryOne(countQuery, clientId).map { it.getInteger(0) }
    }

    fun rxReadCount(countQuery: Query, client: SqlClient, clientId: String? = null): Single<Int> {
        return rxQueryOne(countQuery, client, clientId).map { it.getInteger(0) }
    }

    suspend fun readCount(countCondition: Condition): Int {
        return readCount(getCountQuery(countCondition, table, dsl))
    }

    suspend fun readCount(countCondition: Condition, client: io.vertx.sqlclient.SqlClient): Int {
        return readCount(getCountQuery(countCondition, table, dsl), client)
    }

    fun rxReadCount(countCondition: Condition, clientId: String? = null): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table, dsl), clientId)
    }

    fun rxReadCount(countCondition: Condition, client: SqlClient, clientId: String? = null): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table, dsl), client, clientId)
    }

    suspend fun readCount(countCondition: Condition, table: Table<*>): Int {
        return readCount(getQuery(countCondition, table, dsl))
    }

    suspend fun readCount(countCondition: Condition, table: Table<*>, client: io.vertx.sqlclient.SqlClient): Int {
        return readCount(getCountQuery(countCondition, table, dsl), client)
    }

    fun rxReadCount(countCondition: Condition, table: Table<*>, clientId: String? = null): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table, dsl), clientId)
    }

    fun rxReadCount(
        countCondition: Condition,
        table: Table<*>,
        client: SqlClient,
        clientId: String? = null
    ): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table, dsl), client, clientId)
    }

    private fun getQuery(condition: Condition, table: Table<*>, dsl: DSLContext) =
        dsl.select().from(table).where(condition)

    private fun getCountQuery(condition: Condition, table: Table<*>, dsl: DSLContext) =
        dsl.selectCount().from(table).where(condition)

    abstract suspend fun read(query: Query): List<DTO>
    abstract suspend fun read(query: Query, client: io.vertx.sqlclient.SqlClient): List<DTO>
    abstract fun rxRead(query: Query, clientId: String? = null): Single<List<DTO>>
    abstract fun rxRead(query: Query, client: SqlClient, clientId: String? = null): Single<List<DTO>>

    abstract fun rxReadCount(clientId: String? = null): Single<Int>
    abstract suspend fun readCount(): Int

    abstract suspend fun read(condition: Condition): List<DTO>
    abstract suspend fun read(condition: Condition, client: io.vertx.sqlclient.SqlClient): List<DTO>
    abstract fun rxRead(condition: Condition, clientId: String? = null): Single<List<DTO>>
    abstract fun rxRead(condition: Condition, client: SqlClient, clientId: String? = null): Single<List<DTO>>

    abstract suspend fun readOne(condition: Condition): DTO
    abstract suspend fun readOne(condition: Condition, client: io.vertx.sqlclient.SqlClient): DTO
    abstract fun rxReadOne(condition: Condition, clientId: String? = null): Single<DTO>
    abstract fun rxReadOne(condition: Condition, client: SqlClient, clientId: String? = null): Single<DTO>

    abstract suspend fun readOne(query: Query): DTO
    abstract suspend fun readOne(query: Query, client: io.vertx.sqlclient.SqlClient): DTO
    abstract fun rxReadOne(query: Query, clientId: String? = null): Single<DTO>
    abstract fun rxReadOne(query: Query, client: SqlClient, clientId: String? = null): Single<DTO>

    abstract suspend fun readOptional(query: Query): Optional<DTO>
    abstract suspend fun readOptional(query: Query, client: io.vertx.sqlclient.SqlClient): Optional<DTO>
    abstract fun rxReadOptional(query: Query, clientId: String? = null): Single<Optional<DTO>>
    abstract fun rxReadOptional(query: Query, client: SqlClient, clientId: String? = null): Single<Optional<DTO>>

    abstract suspend fun readOptional(condition: Condition): Optional<DTO>
    abstract suspend fun readOptional(condition: Condition, client: io.vertx.sqlclient.SqlClient): Optional<DTO>
    abstract fun rxReadOptional(condition: Condition, clientId: String? = null): Single<Optional<DTO>>
    abstract fun rxReadOptional(
        condition: Condition,
        client: SqlClient,
        clientId: String? = null
    ): Single<Optional<DTO>>

    abstract fun map(row: Row, table: Table<RECORD>, offset: Int = 0): DTO
    abstract fun map(row: Row, offset: Int = 0): DTO
    abstract fun map(row: io.vertx.reactivex.sqlclient.Row, offset: Int = 0): DTO

    abstract fun map(rs: RowSet<Row>, table: Table<RECORD>, offset: Int = 0): List<DTO>
    abstract fun map(rs: RowSet<Row>, offset: Int = 0): List<DTO>
    abstract fun map(
        rs: io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>,
        offset: Int = 0
    ): List<DTO>

    // ----------- CRUD helper methods

    fun tsVector(language: String, text: String): Field<Any> {
        return JooqHelper.tsVector(language, text)
    }

    fun tsQuery(language: String, searchTerm: String): Condition {
        return JooqHelper.tsQuery(language, searchTerm)
    }

    fun stWithin(left: Field<*>, right: Field<*>): Condition {
        return JooqHelper.stWithin(left, right)
    }

    fun stWithin(left: Field<*>, x: Double, y: Double, distance: Int): Condition {
        return JooqHelper.stWithin(left, x, y, distance)
    }

    fun stDWithin(left: Field<*>, location: PointDTO, distance: Int): Condition {
        return JooqHelper.stDWithin(left, location, distance)
    }

    fun stIntersects(left: Field<*>, minX: Double, minY: Double, maxX: Double, maxY: Double): Condition {
        return JooqHelper.stIntersects(left, minX, minY, maxX, maxY)
    }

    fun stDWithin(left: Field<*>, x: Double, y: Double, distance: Int): Condition {
        return JooqHelper.stDWithin(left, x, y, distance)
    }

    fun stDistance(location: Field<*>, x: Double, y: Double): Condition {
        return JooqHelper.stDistance(location, x, y)
    }

    fun stDistance(x: Double, y: Double): Condition {
        return JooqHelper.stDistance(x, y)
    }

    fun any(id: Int, field: Field<*>): Condition {
        return JooqHelper.any(id, field)
    }

    fun toStringList(res: RowSet<Row>): List<String> {
        return JooqHelper.toStringList(res)
    }

    fun toUUIDList(res: RowSet<Row>): List<UUID> {
        return JooqHelper.toUUIDList(res)
    }

    fun toIntegerList(res: RowSet<Row>): List<Int> {
        return JooqHelper.toIntegerList(res)
    }

    protected fun date(timestamp: LocalDateTime): LocalDate? {
        return JooqHelper.date(timestamp)
    }

    protected fun dateTime(date: LocalDate?): LocalDateTime? {
        return JooqHelper.dateTime(date)
    }

    // conversion helpers
    protected fun stringArray(list: List<String>?): Array<String>? {
        return list?.toTypedArray()
    }

    protected fun integerArray(list: List<Int>?): Array<Int>? {
        return list?.toTypedArray()
    }

    protected fun uuid(): UUID {
        return UUID.randomUUID()
    }

    protected fun uuidList(row: Row, i: Int): List<UUID>? {
        return DaoHelperCommon.uuidList(row, i)
    }

    protected fun stringList(row: Row, i: Int): List<String>? {
        return DaoHelperCommon.stringList(row, i)
    }

    protected fun integerList(row: Row, i: Int): List<Int>? {
        return DaoHelperCommon.integerList(row, i)
    }

    protected fun charList(row: Row, i: Int): List<Char>? {
        return DaoHelperCommon.charList(row, i)
    }

    protected fun <O> `object`(row: Row, i: Int, type: Class<O>): O? {
        return DaoHelperCommon.`object`(row, i, type)
    }

    protected fun <X, E : TypedEnum<X>> enumeration(field: Field<E>, row: Row, i: Int): E? {
        return DaoHelperCommon.enumeration(field, row, i)
    }

    protected fun <X> dto(row: Row, i: Int, type: Class<X>): X? {
        return DaoHelperCommon.dto(row, i, type)
    }

    protected fun <X> list(row: Row, i: Int, type: Class<X>): List<X>? {
        return DaoHelperCommon.list(row, i, type)
    }

    protected fun <O> jsonObject(o: O?): JsonObject? {
        return if (o == null) null else JsonObject.mapFrom(o)
    }

    protected fun <O> jsonArray(o: List<O>?): JsonArray? {
        return if (o == null) null else JsonArray(o)
    }

    private fun getBaseValues(orderBy: List<OrderDTO>, baseValues: List<Any>?): Array<Any?> {
        if (baseValues == null) return emptyArray()
        val fieldsAndValues = orderBy.map { it.field }.zip(baseValues)
        return fieldsAndValues.map { (field, value) ->
            getBaseValue(value, field)
        }.toTypedArray()
    }


    fun getBaseValue(jsonStringBaseValue: Any?, fieldName: String): Any? {
        val baseValue = jsonStringBaseValue ?: return null
        val dbField = getDbField(fieldName)
        if (dbField.type.isInstance(baseValue)) {
            return baseValue
        }
        return DatabindCodec.mapper().convertValue(baseValue, dbField.type)
    }

    protected fun validate(dto: Any?) {
        val validator = context.validator()
        if (dto != null && validator != null) {
            validator.validate(dto)?.let { validationErrors ->
                if (validationErrors.isNotEmpty()) {
                    throw ConstraintViolationException(validationErrors)
                }
            }
        }
    }

    /**
     * Adds the new condition the a given existing condition with AND.
     * Will return the new condition if the existing condition was null.
     */
    fun getSearchConditionAnd(currentCondition: Condition?, newCondition: Condition): Condition {
        return DaoHelperCommon.getSearchConditionAnd(currentCondition, newCondition)
    }

    /**
     * Adds the new condition the a given existing condition with OR.
     * Will return the new condition if the existing condition was null.
     */
    fun getSearchConditionOr(currentCondition: Condition?, newCondition: Condition): Condition {
        return DaoHelperCommon.getSearchConditionOr(currentCondition, newCondition)
    }
}
