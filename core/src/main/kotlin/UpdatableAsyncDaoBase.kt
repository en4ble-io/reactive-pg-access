package io.en4ble.pgaccess

import io.en4ble.pgaccess.DatabaseContext.Companion.toSingleDatabaseContext
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.mappers.JooqMapper
import io.en4ble.pgaccess.util.DaoHelper
import io.en4ble.pgaccess.util.DaoHelperCommon
import io.en4ble.pgaccess.util.RxDaoHelper
import io.reactivex.Single
import io.vertx.reactivex.sqlclient.SqlClient
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import org.jooq.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class UpdatableAsyncDaoBase<RECORD : UpdatableRecord<RECORD>, DTO, ID>
constructor(
    context: DatabaseContext,
    table: Table<RECORD>,
    dtoType: Class<DTO>
) : AsyncDaoBase<RECORD, DTO>(context, table, dtoType) {

    /** The field that contains the UPDATED timestamp.  */
    open fun getUpdatedField(): TableField<RECORD, LocalDateTime>? {
        return null
    }

    suspend fun delete(condition: Condition): Int {
        return DaoHelper.update(context.dsl().deleteFrom(table).where(condition), context)
    }

    suspend fun delete(condition: Condition, context: DatabaseContext): Int {
        return DaoHelper.update(context.dsl().deleteFrom(table).where(condition), context)
    }

    fun rxDelete(condition: Condition): Single<Int> {
        return RxDaoHelper.update(context.dsl().deleteFrom(table).where(condition), toSingleDatabaseContext(context))
    }

    fun rxDelete(condition: Condition, context: DatabaseContext): Single<Int> {
        return RxDaoHelper.update(context.dsl().deleteFrom(table).where(condition), toSingleDatabaseContext(context))
    }

    suspend fun update(query: Query, context: DatabaseContext): Int {
        return DaoHelper.update(query, context)
    }

    suspend fun update(query: Query, client: io.vertx.sqlclient.SqlClient, context: DatabaseContext): Int {
        return DaoHelper.update(query, client, toSingleDatabaseContext(context))
    }

    fun rxUpdate(query: Query, context: DatabaseContext): Single<Int> {
        return RxDaoHelper.update(query, toSingleDatabaseContext(context))
    }

    fun rxUpdate(query: Query, client: SqlClient, context: DatabaseContext): Single<Int> {
        return RxDaoHelper.update(query, client, toSingleDatabaseContext(context))
    }

    suspend fun update(query: Query): Int {
        return DaoHelper.update(query, context)
    }

    suspend fun update(query: Query, client: io.vertx.sqlclient.SqlClient): Int {
        return DaoHelper.update(query, client, toSingleDatabaseContext(context))
    }

    fun rxUpdate(query: Query): Single<Int> {
        return RxDaoHelper.update(query, toSingleDatabaseContext(context))
    }

    fun rxUpdate(query: Query, client: SqlClient): Single<Int> {
        return RxDaoHelper.update(query, client, toSingleDatabaseContext(context))
    }

    fun addLimit(query: SelectLimitStep<*>, page: PagingDTO): SelectForUpdateStep<*> {
        return DaoHelperCommon.addLimit(query, page)
    }

//    suspend fun <T1, T2, T3, T4, T, R:Record,X> updateReturning(
//        update1: Pair<Field<T1>, T1?>,
//        update2: Pair<Field<T2>, T2?>,
//        update3: Pair<Field<T3>, T3?>,
//        update4: Pair<Field<T4>, T4?>,
//        condition: Condition,
//        table:Table<R>
//        vararg fields:Field<T>
//    ): X {
//        val query = getUpdateQuery(update1, update2, update3, update4, condition)
//            .returning(*fields)
//        return map(DaoHelper.queryOne(query,context),table)
//    }

    suspend fun <T1> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(update1, condition)
            .returning(*table.fields())
        return read(query)
    }

    fun <T1> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(update1, condition)
            .returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(update1, update2, condition)
            .returning(*table.fields())
        return read(query)
    }

    fun <T1, T2> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(update1, update2, update3, condition)
            .returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(update1, update2, update3, update4, condition)
            .returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5, T6> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition
    ): List<DTO> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        ).returning(*table.fields())
        return read(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition
    ): Single<List<DTO>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        ).returning(*table.fields())
        return rxRead(query)
    }

    suspend fun <T1, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(update1, condition)
            .returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(update1, update2, condition)
            .returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(update1, update2, update3, condition)
            .returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(update1, update2, update3, update4, condition)
            .returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, T6, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, T7, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> updateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): List<R> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        ).returning(*fields)
        return mapper.toList(query(query))
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> rxUpdateReturning(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition,
        mapper: JooqMapper<R>,
        vararg fields: SelectFieldOrAsterisk
    ): Single<List<R>> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        ).returning(*fields)
        return rxQuery(query).map { mapper.toList(it.delegate as RowSet<Row>) }
    }

    suspend fun <T1> update(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            condition
        )
        return update(query)
    }

    fun <T1> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            condition
        )
        return update(query)
    }

    fun <T1, T2> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5, T6> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            condition
        )
        return rxUpdate(query)
    }

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> update(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition
    ): Int {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        )
        return update(query)
    }

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> rxUpdate(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition
    ): Single<Int> {
        val query = getUpdateQuery(
            update1,
            update2,
            update3,
            update4,
            update5,
            update6,
            update7,
            update8,
            update9,
            update10,
            condition
        )
        return rxUpdate(query)
    }

    private fun <T1> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        val step = addUpdate(update1.first, update1.second, update)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5, T6> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        step = addUpdate(update6.first, update6.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5, T6, T7> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        step = addUpdate(update6.first, update6.second, step)
        step = addUpdate(update7.first, update7.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5, T6, T7, T8> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        step = addUpdate(update6.first, update6.second, step)
        step = addUpdate(update7.first, update7.second, step)
        step = addUpdate(update8.first, update8.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        step = addUpdate(update6.first, update6.second, step)
        step = addUpdate(update7.first, update7.second, step)
        step = addUpdate(update8.first, update8.second, step)
        step = addUpdate(update9.first, update9.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    private fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> getUpdateQuery(
        update1: Pair<Field<T1>, T1?>,
        update2: Pair<Field<T2>, T2?>,
        update3: Pair<Field<T3>, T3?>,
        update4: Pair<Field<T4>, T4?>,
        update5: Pair<Field<T5>, T5?>,
        update6: Pair<Field<T6>, T6?>,
        update7: Pair<Field<T7>, T7?>,
        update8: Pair<Field<T8>, T8?>,
        update9: Pair<Field<T9>, T9?>,
        update10: Pair<Field<T10>, T10?>,
        condition: Condition
    ): UpdateConditionStep<RECORD> {
        val update = update()
        var step: UpdateSetStep<RECORD>?
        step = addUpdate(update1.first, update1.second, update)
        step = addUpdate(update2.first, update2.second, step)
        step = addUpdate(update3.first, update3.second, step)
        step = addUpdate(update4.first, update4.second, step)
        step = addUpdate(update5.first, update5.second, step)
        step = addUpdate(update6.first, update6.second, step)
        step = addUpdate(update7.first, update7.second, step)
        step = addUpdate(update8.first, update8.second, step)
        step = addUpdate(update9.first, update9.second, step)
        step = addUpdate(update10.first, update10.second, step)
        return (step as UpdateSetMoreStep)
            .where(condition)
    }

    suspend fun update(
        condition: Condition,
        fields: Array<Field<Any>>,
        values: Array<Any?>
    ): DTO {
        if (fields.size != values.size) {
            throw RuntimeException("fields and values must match in lengths")
        }
        val update = update()
        var step: UpdateSetStep<RECORD>? = null
        var firstValueIndex: Int = -1
        for (i in 0..fields.size) {
            val field = fields[i]
            val value = values[i]
            if (value != null) {
                step = addUpdate(field, value, update)
                firstValueIndex = i
                break
            }
        }
        if (step == null) {
            throw RuntimeException("query does not contain any updates")
        }

        for (i in firstValueIndex..fields.size) {
            val field = fields[i]
            val value = values[i]
            if (value != null) {
                step = addUpdate(field, value, step!!)
                break
            }
        }
        val query = (step as UpdateSetMoreStep)
            .where(condition)
            .returning(*table.fields())
        return readOne(query)
    }

    suspend fun <VALUE> update(
        fields: List<Field<VALUE>>,
        values: List<VALUE?>,
        condition: Condition
    ): DTO {
        if (fields.size != values.size) {
            throw RuntimeException("fields and values must match in lengths")
        }
        val update = update()
        var step: UpdateSetStep<RECORD>? = null
        var firstValueIndex: Int = -1
        for (i in 0..fields.size) {
            val field = fields[i]
            val value = values[i]
            if (value != null) {
                step = addUpdate(field, value, update)
                firstValueIndex = i
                break
            }
        }
        if (step == null) {
            throw RuntimeException("query does not contain any updates")
        }

        for (i in firstValueIndex..fields.size) {
            val field = fields[i]
            val value = values[i]
            if (value != null) {
                step = addUpdate(field, value, step!!)
                break
            }
        }
        val query = (step as UpdateSetMoreStep)
            .where(condition)
            .returning(*table.fields())
        return readOne(query)
    }

    suspend fun update(update: UpdateSetStep<RECORD>, condition: Condition): Int {
        if (update !is UpdateSetMoreStep<RECORD>) {
            return 0
        }
        return update(update.where(condition))
    }

    fun update(): UpdateSetStep<RECORD> {
        return getUpdate(table(), getUpdatedField())
    }

    private fun getUpdate(
        table: Table<RECORD>,
        updatedField: TableField<RECORD, LocalDateTime>?
    ): UpdateSetStep<RECORD> {
        val update = context.dsl().update(table)
        return if (updatedField == null) update else update.set(updatedField, now())
    }

    fun <VALUE> addUpdate(
        field: TableField<RECORD, VALUE>,
        value: VALUE?,
        firstStep: UpdateSetStep<RECORD>,
        nextStep: UpdateSetMoreStep<RECORD>?
    ): UpdateSetStep<RECORD> {
        if (value != null) {
            return if (nextStep == null) {
                firstStep.set(field, value)
            } else {
                nextStep.set(field, value)
            }
        }
        if (nextStep != null) {
            return nextStep
        }
        return firstStep
    }

    fun <VALUE> addUpdate(
        field: Field<VALUE>,
        value: VALUE?,
        nextStep: UpdateSetStep<RECORD>
    ): UpdateSetStep<RECORD> {
        if (value != null) {
            return nextStep.set(field, value)
        }
        return nextStep
    }
}
