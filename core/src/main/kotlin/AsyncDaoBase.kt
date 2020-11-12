package io.en4ble.pgaccess

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
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class AsyncDaoBase<RECORD : Record, DTO>
protected constructor(
    @Suppress("CanBeParameter") protected val context: DatabaseContext,
    protected val table: Table<RECORD>,
    private val dtoType: Class<DTO>
) {
    protected val LOG by lazy { LoggerFactory.getLogger(AsyncDaoBase::class.java) }

    protected val dsl: DSLContext = context.dsl

    protected val sqlClient: SqlClient = context.sqlClient

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

    suspend fun queryOptional(query: Query): Optional<Row> {
        return DaoHelper.queryOptional(query, context)
    }

    suspend fun queryOptional(query: Query, client: io.vertx.sqlclient.SqlClient): Optional<Row> {
        return DaoHelper.queryOptional(query, client, context)
    }

    fun rxQueryOptional(query: Query): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.queryOptional(query, context)
    }

    fun rxQueryOptional(query: Query, client: SqlClient): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.queryOptional(query, client, context)
    }

    suspend fun queryOptional(query: Query, context: DatabaseContext): Optional<Row> {
        return DaoHelper.queryOptional(query, context)
    }

    suspend fun queryOptional(
        query: Query,
        client: io.vertx.sqlclient.SqlClient,
        context: DatabaseContext
    ): Optional<Row> {
        return DaoHelper.queryOptional(query, client, context)
    }

    fun rxQueryOptional(
        query: Query,
        context: DatabaseContext
    ): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.queryOptional(query, context)
    }

    fun rxQueryOptional(
        query: Query, client: SqlClient, context: DatabaseContext
    ): Single<Optional<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.queryOptional(query, client, context)
    }

    suspend fun query(condition: Condition, context: DatabaseContext): RowSet<Row> {
        return DaoHelper.query(getQuery(condition, table()), context)
    }

    suspend fun query(
        condition: Condition,
        client: io.vertx.sqlclient.SqlClient,
        context: DatabaseContext
    ): RowSet<Row> {
        return DaoHelper.query(getQuery(condition, table()), client, context)
    }

    fun rxQuery(
        condition: Condition, client: SqlClient, context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(getQuery(condition, table()), client, context)
    }

    fun rxQuery(
        condition: Condition,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(getQuery(condition, table()), context)
    }

    suspend fun query(query: Query, context: DatabaseContext): RowSet<Row> {
        return DaoHelper.query(query, context)
    }

    suspend fun query(query: Query, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): RowSet<Row> {
        return DaoHelper.query(query, client, context)
    }

    fun rxQuery(
        query: Query,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(query, context)
    }

    fun rxQuery(
        query: Query,
        client: SqlClient,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(query, client, context)
    }

    suspend fun queryOne(condition: Condition, context: DatabaseContext): Row {
        return DaoHelper.queryOne(getQuery(condition, table()), context)
    }

    suspend fun queryOne(condition: Condition, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): Row {
        return DaoHelper.queryOne(getQuery(condition, table()), client, context)
    }

    fun rxQueryOne(
        condition: Condition,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(getQuery(condition, table()), context)
    }

    fun rxQueryOne(
        condition: Condition, client: SqlClient, context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(getQuery(condition, table()), client, context)
    }

    suspend fun queryOne(query: Query, context: DatabaseContext): Row {
        return DaoHelper.queryOne(query, context)
    }

    suspend fun queryOne(query: Query, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): Row {
        return DaoHelper.queryOne(query, client, context)
    }

    suspend fun query(query: Query): RowSet<Row> {
        return DaoHelper.query(query, context)
    }

    suspend fun query(query: Query, client: io.vertx.sqlclient.SqlClient): RowSet<Row> {
        return DaoHelper.query(query, client, context)
    }

    suspend fun queryOne(query: Query): Row {
        return DaoHelper.queryOne(query, context)
    }

    suspend fun queryOne(query: Query, client: io.vertx.sqlclient.SqlClient): Row {
        return DaoHelper.queryOne(query, client, context)
    }

    fun rxQueryOne(query: Query, context: DatabaseContext): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(query, context)
    }

    fun rxQueryOne(
        query: Query,
        client: SqlClient,
        context: DatabaseContext
    ): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(query, client, context)
    }

    fun rxQuery(query: Query): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(query, context)
    }

    fun rxQuery(
        query: Query,
        client: SqlClient
    ): Single<io.vertx.reactivex.sqlclient.RowSet<io.vertx.reactivex.sqlclient.Row>> {
        return RxDaoHelper.query(query, client, context)
    }

    fun rxQueryOne(query: Query): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(query, context)
    }

    fun rxQueryOne(query: Query, client: SqlClient): Single<io.vertx.reactivex.sqlclient.Row> {
        return RxDaoHelper.queryOne(query, client, context)
    }

    suspend fun readUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null
    ): List<UUID> {
        return readUUIDs(idField, condition, page, context.sqlClient.delegate)
    }

    suspend fun readUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null, client: io.vertx.sqlclient.SqlClient
    ): List<UUID> {
        val query = dsl.select(idField).from(idField.table).where(condition)
        return DaoHelper.readUUIDs(query, page, client, context)
    }

    fun rxReadUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null
    ): Single<List<UUID>> {
        return rxReadUUIDs(idField, condition, page, context.sqlClient)
    }

    fun rxReadUUIDs(
        idField: TableField<*, *>, condition: Condition, page: PagingDTO? = null, client: SqlClient
    ): Single<List<UUID>> {
        val query = dsl.select(idField).from(idField.table).where(condition)
        return RxDaoHelper.readUUIDs(query, page, client, context)
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null): List<UUID> {
        return DaoHelper.readUUIDs(query, page, context)
    }

    suspend fun readUUIDs(query: Query, page: PagingDTO? = null, client: SqlClient): List<UUID> {
        return DaoHelper.readUUIDs(query, page, client.delegate, context)
    }

    fun rxReadUUIDs(query: Query, page: PagingDTO? = null): Single<List<UUID>> {
        return RxDaoHelper.readUUIDs(query, page, context)
    }

    fun rxReadUUIDs(query: Query, page: PagingDTO? = null, client: SqlClient): Single<List<UUID>> {
        return RxDaoHelper.readUUIDs(query, page, client, context)
    }

    fun <ID> rxReadPage(page: PageDTO<ID>): Single<List<DTO>> {
        return rxReadPage(null, page)
    }

    fun <ID> rxReadPage(condition: Condition? = null, page: PageDTO<ID>): Single<List<DTO>> {
        return rxReadPage(
            condition,
            page.baseId,
            arrayOf(getBaseValue(page.baseValue, page.orderBy.field)),
            listOf(page.orderBy),
            page.size
        )
    }

    fun <ID> rxReadPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValue: Any?,
        orderBy: OrderDTO,
        pageSize: Int
    ): Single<List<DTO>> {
        return rxReadPage(condition, baseId, arrayOf(baseValue), listOf(orderBy), pageSize)
    }

    fun <ID> rxReadPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValue: Any?,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): Single<List<DTO>> {
        return rxReadPage(condition, baseId, arrayOf(baseValue), orderBy, pageSize)
    }

    fun <ID> rxReadPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): Single<List<DTO>> {
        checkValuesAndOrderBy(baseValues, orderBy)
        val baseQuery = getReadPageQuery(condition, orderBy, baseId)
        return if (baseId != null) {
            val array = arrayOf(*baseValues, baseId)
            val query = baseQuery.seek(*array)
            rxQuery(query.limit(pageSize))
        } else {
            rxQuery(baseQuery.limit(pageSize))
        }.map { map(it.delegate as RowSet<Row>) }
    }

    suspend fun <ID> readPage(page: PageDTO<ID>): List<DTO> {
        return readPage(null, page)
    }

    suspend fun <ID> readPage(condition: Condition? = null, page: PageDTO<ID>): List<DTO> {
        return readPage(
            condition,
            page.baseId,
            arrayOf(getBaseValue(page.baseValue, page.orderBy.field)),
            listOf(page.orderBy),
            page.size
        )
    }

    suspend fun <ID> readPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValue: Any?,
        orderBy: OrderDTO,
        pageSize: Int
    ): List<DTO> {
        return readPage(condition, baseId, arrayOf(baseValue), listOf(orderBy), pageSize)
    }

    suspend fun <ID> readPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValue: Any?,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): List<DTO> {
        return readPage(condition, baseId, arrayOf(baseValue), orderBy, pageSize)
    }

    /**
     * Reads a page using "keyset pagination" via jOOQs seek method.
     * @param baseId The id of the last entry of the last page.
     * @param baseValue The value of the last entry of the last page that is used for sorting. Must match the type of the field in the OrderDTO.
     * @param order Information on how to sort the list.
     * @param pageSize The maximum number of results to return.
     */
    suspend fun <ID> readPage(
        condition: Condition? = null,
        baseId: ID?,
        baseValues: Array<Any?>,
        orderBy: List<OrderDTO>,
        pageSize: Int
    ): List<DTO> {
        checkValuesAndOrderBy(baseValues, orderBy)
        val baseQuery = getReadPageQuery(condition, orderBy, baseId)
        val res = if (baseId != null) {
            val array = arrayOf(*baseValues, baseId)
            val query = baseQuery.seek(*array)
            query(query.limit(pageSize))
        } else {
            query(baseQuery.limit(pageSize))
        }

        return map(res)
    }

    protected fun checkValuesAndOrderBy(baseValues: Array<Any?>, orderBy: List<OrderDTO>) {
        val baseValuesSize = baseValues.size
        val orderBySize = orderBy.size
        if (baseValuesSize != orderBySize) {
            throw ValidationException("baseValues size ($baseValuesSize) does not match orderBy size ($orderBySize)")
        }
        // baseValue type must match type of orderBy field
        for (i in baseValues.indices) {
            val baseValue = baseValues[i] ?: continue
            val orderField = orderBy[i].field
            val dbField = getDbField(orderField)
            if (!dbField.type.isInstance(baseValue)) {
                throw RuntimeException("order field has type ${baseValue.javaClass} but must be ${dbField.type}")
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

    protected fun getReadPageQuery(
        condition: Condition,
        offset: Int,
        orderBy: List<OrderDTO>
    ): SelectLimitAfterOffsetStep<Record> {
        val select = dsl.select().from(table()).where(condition)
        return select.orderBy(getOrderByList(orderBy)).offset(offset)
    }

    protected fun getOrderByList(orderBy: List<OrderDTO>) =
        orderBy.map { order -> getOrderBy(order) }.toMutableList()

    protected fun getOrderBy(order: OrderDTO): SortField<*> {
        val sortDirection = order.direction
        val orderField = getDbField(order.field)
        return if (sortDirection === SortDirection.ASC)
            orderField.asc()
        else
            orderField.desc()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <ID> getReadPageQuery(
        condition: Condition?,
        orderBy: List<OrderDTO>,
        baseId: ID?
    ): SelectSeekStepN<Record> {
        val idField = primaryKeyField<ID>()
            ?: throw RuntimeException("The read page query requires a primary key field in the table/view ${table().name}.\n" +
                " Either add a primary key to the table or override the function primaryKeyField() in ${this.javaClass.name} to return the desired primary key field of the view\n" +
                " example:\n" +
                "    override fun <ID> primaryKeyField(): TableField<ProfileListRecord, ID>? {\n" +
                "        return ProfileList.PROFILE_LIST.ID as TableField<ProfileListRecord, ID>\n" +
                "    }")
        val baseSelect = dsl.select().from(table())
        val select =
            if (condition != null) {
                baseSelect.where(condition)
            } else {
                baseSelect
            }

        if (baseId != null && !idField.type.isInstance(baseId)) {
            throw ValidationException("Given base id does not match type of database id (${idField.type}")
        }

        val orderByList = getOrderByList(orderBy)
        orderByList.add(idField.desc())
        return select.orderBy(*orderByList.toTypedArray())
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

    fun rxReadAll(): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table)
    }

    fun rxReadAll(table: Table<RECORD>): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table)
    }

    fun rxReadAll(client: SqlClient): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table, client)
    }

    fun rxReadAll(table: Table<RECORD>? = null, client: SqlClient): Single<List<DTO>> {
        return rxRead(dsl.select().from(table), table ?: this.table, client)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>): List<DTO> {
        return read(getQuery(condition, table), table)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(getQuery(condition, table), table, client)
    }

    fun rxRead(condition: Condition, table: Table<RECORD>): Single<List<DTO>> {
        return rxRead(getQuery(condition, table), table)
    }

    fun rxRead(condition: Condition, table: Table<RECORD>, client: SqlClient): Single<List<DTO>> {
        return rxRead(getQuery(condition, table), table, client)
    }

    suspend fun read(condition: Condition, table: Table<RECORD>, page: PagingDTO?): List<DTO> {
        return read(getQuery(condition, table), table, page)
    }

    suspend fun read(
        condition: Condition,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: io.vertx.sqlclient.SqlClient
    ): List<DTO> {
        return read(getQuery(condition, table), table, page, client)
    }

    fun rxRead(condition: Condition, table: Table<RECORD>, page: PagingDTO?): Single<List<DTO>> {
        return rxRead(getQuery(condition, table), table, page)
    }

    fun rxRead(condition: Condition, table: Table<RECORD>, page: PagingDTO?, client: SqlClient): Single<List<DTO>> {
        return rxRead(getQuery(condition, table), table, page, client)
    }

    suspend fun read(condition: Condition, orderBy: List<OrderDTO>): List<DTO> {
        return read(condition, table(), PagingDTO(orderBy))
    }

    suspend fun read(condition: Condition, orderBy: List<OrderDTO>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(condition, table(), PagingDTO(orderBy), client)
    }

    fun rxRead(condition: Condition, orderBy: List<OrderDTO>): Single<List<DTO>> {
        return rxRead(condition, table(), PagingDTO(orderBy))
    }

    fun rxRead(condition: Condition, orderBy: List<OrderDTO>, client: SqlClient): Single<List<DTO>> {
        return rxRead(condition, table(), PagingDTO(orderBy), client)
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

    fun rxRead(condition: Condition, table: Table<RECORD>, orderBy: List<OrderDTO>): Single<List<DTO>> {
        return rxRead(condition, table, PagingDTO(orderBy))
    }

    fun rxRead(
        condition: Condition,
        table: Table<RECORD>,
        orderBy: List<OrderDTO>,
        client: SqlClient
    ): Single<List<DTO>> {
        return rxRead(condition, table, PagingDTO(orderBy), client)
    }

    suspend fun read(query: Query, table: Table<RECORD>): List<DTO> {
        return read(query, table, null)
    }

    suspend fun read(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): List<DTO> {
        return read(query, table, null, client)
    }

    fun rxRead(query: Query, table: Table<RECORD>): Single<List<DTO>> {
        return rxRead(query, table, null)
    }

    fun rxRead(query: Query, table: Table<RECORD>, client: SqlClient): Single<List<DTO>> {
        return rxRead(query, table, null, client)
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
        return DaoHelper.read(query, table, page, client, context).map { map(it, table) }
    }

    fun rxRead(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?
    ): Single<List<DTO>> {
        return RxDaoHelper.read(query, table, page, context).map { map(it.delegate as RowSet<Row>, table) }
    }

    fun rxRead(
        query: Query,
        table: Table<RECORD>,
        page: PagingDTO?,
        client: SqlClient
    ): Single<List<DTO>> {
        return RxDaoHelper.read(query, table, page, client, context).map { map(it.delegate as RowSet<Row>, table) }
    }

    suspend fun readOne(condition: Condition, table: Table<RECORD>): DTO {
        return readOne(getQuery(condition, table), table)
    }

    suspend fun readOne(condition: Condition, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): DTO {
        return readOne(getQuery(condition, table), table, client)
    }

    fun rxReadOne(condition: Condition, table: Table<RECORD>): Single<DTO> {
        return rxReadOne(getQuery(condition, table), table)
    }

    fun rxReadOne(condition: Condition, table: Table<RECORD>, client: SqlClient): Single<DTO> {
        return rxReadOne(getQuery(condition, table), table, client)
    }

    suspend fun readOne(query: Query, table: Table<RECORD>): DTO {
        return map(queryOne(query), table)
    }

    suspend fun readOne(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): DTO {
        return map(queryOne(query, client), table)
    }

    fun rxReadOne(query: Query, table: Table<RECORD>): Single<DTO> {
        return rxQueryOne(query).map {
            map(it.delegate, table)
        }
    }

    fun rxReadOne(query: Query, table: Table<RECORD>, client: SqlClient): Single<DTO> {
        return rxQueryOne(query, client).map {
            map(it.delegate, table)
        }
    }

    suspend fun readOptional(condition: Condition, table: Table<RECORD>): Optional<DTO> {
        return readOptional(getQuery(condition, table), table)
    }

    suspend fun readOptional(
        condition: Condition,
        table: Table<RECORD>,
        client: io.vertx.sqlclient.SqlClient
    ): Optional<DTO> {
        return readOptional(getQuery(condition, table), table, client)
    }

    fun rxReadOptional(condition: Condition, table: Table<RECORD>): Single<Optional<DTO>> {
        return rxReadOptional(getQuery(condition, table), table)
    }

    fun rxReadOptional(condition: Condition, table: Table<RECORD>, client: SqlClient): Single<Optional<DTO>> {
        return rxReadOptional(getQuery(condition, table), table, client)
    }

    suspend fun readOptional(query: Query, table: Table<RECORD>): Optional<DTO> {
        return readOptional(query, table, context.sqlClient.delegate)
    }

    suspend fun readOptional(query: Query, table: Table<RECORD>, client: io.vertx.sqlclient.SqlClient): Optional<DTO> {
        val res = queryOptional(query, client)
        return if (res.isPresent) {
            Optional.of(map(res.get(), table))
        } else {
            Optional.empty()
        }
    }

    fun rxReadOptional(query: Query, table: Table<RECORD>): Single<Optional<DTO>> {
        return rxReadOptional(query, table, context.sqlClient)
    }

    fun rxReadOptional(query: Query, table: Table<RECORD>, client: SqlClient): Single<Optional<DTO>> {
        return rxQueryOptional(query, client).map {
            if (it.isPresent) {
                Optional.of(map(it.get().delegate, table))
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

    fun rxReadCount(countQuery: Query): Single<Int> {
        return rxQueryOne(countQuery).map { it.getInteger(0) }
    }

    fun rxReadCount(countQuery: Query, client: SqlClient): Single<Int> {
        return rxQueryOne(countQuery, client).map { it.getInteger(0) }
    }

    suspend fun readCount(countCondition: Condition): Int {
        return readCount(getCountQuery(countCondition, table))
    }

    suspend fun readCount(countCondition: Condition, client: io.vertx.sqlclient.SqlClient): Int {
        return readCount(getCountQuery(countCondition, table), client)
    }

    fun rxReadCount(countCondition: Condition): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table))
    }

    fun rxReadCount(countCondition: Condition, client: SqlClient): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table), client)
    }

    suspend fun readCount(countCondition: Condition, table: Table<*>): Int {
        return readCount(getQuery(countCondition, table))
    }

    suspend fun readCount(countCondition: Condition, table: Table<*>, client: io.vertx.sqlclient.SqlClient): Int {
        return readCount(getCountQuery(countCondition, table), client)
    }

    fun rxReadCount(countCondition: Condition, table: Table<*>): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table))
    }

    fun rxReadCount(countCondition: Condition, table: Table<*>, client: SqlClient): Single<Int> {
        return rxReadCount(getCountQuery(countCondition, table), client)
    }

    private fun getQuery(condition: Condition, table: Table<*>) = dsl.select().from(table).where(condition)
    private fun getCountQuery(condition: Condition, table: Table<*>) = dsl.selectCount().from(table).where(condition)

    abstract suspend fun read(query: Query): List<DTO>
    abstract suspend fun read(query: Query, client: io.vertx.sqlclient.SqlClient): List<DTO>
    abstract fun rxRead(query: Query): Single<List<DTO>>
    abstract fun rxRead(query: Query, client: SqlClient): Single<List<DTO>>

    abstract fun rxReadCount(): Single<Int>
    abstract suspend fun readCount(): Int

    abstract suspend fun read(condition: Condition): List<DTO>
    abstract suspend fun read(condition: Condition, client: io.vertx.sqlclient.SqlClient): List<DTO>
    abstract fun rxRead(condition: Condition): Single<List<DTO>>
    abstract fun rxRead(condition: Condition, client: SqlClient): Single<List<DTO>>

    abstract suspend fun readOne(condition: Condition): DTO
    abstract suspend fun readOne(condition: Condition, client: io.vertx.sqlclient.SqlClient): DTO
    abstract fun rxReadOne(condition: Condition): Single<DTO>
    abstract fun rxReadOne(condition: Condition, client: SqlClient): Single<DTO>

    abstract suspend fun readOne(query: Query): DTO
    abstract suspend fun readOne(query: Query, client: io.vertx.sqlclient.SqlClient): DTO
    abstract fun rxReadOne(query: Query): Single<DTO>
    abstract fun rxReadOne(query: Query, client: SqlClient): Single<DTO>

    abstract suspend fun readOptional(query: Query): Optional<DTO>
    abstract suspend fun readOptional(query: Query, client: io.vertx.sqlclient.SqlClient): Optional<DTO>
    abstract fun rxReadOptional(query: Query): Single<Optional<DTO>>
    abstract fun rxReadOptional(query: Query, client: SqlClient): Single<Optional<DTO>>

    abstract suspend fun readOptional(condition: Condition): Optional<DTO>
    abstract suspend fun readOptional(condition: Condition, client: io.vertx.sqlclient.SqlClient): Optional<DTO>
    abstract fun rxReadOptional(condition: Condition): Single<Optional<DTO>>
    abstract fun rxReadOptional(condition: Condition, client: SqlClient): Single<Optional<DTO>>

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

    fun tsVector(language: String, text: String): Field<Any>? {
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

    fun getBaseValue(jsonStringBaseValue: String?, fieldName: String): Any? {
        val baseValue = jsonStringBaseValue ?: return null
        val dbField = getDbField(fieldName)
        if (dbField.type.isInstance(baseValue)) {
            return baseValue
        }
        return DatabindCodec.mapper().convertValue(baseValue, dbField.type)
    }

    protected fun validate(dto: Any?) {
        if (dto != null && context.validator != null) {
            val validationErrors = context.validator.validate(dto)
            if (validationErrors.isNotEmpty()) {
                throw ConstraintViolationException(validationErrors)
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
