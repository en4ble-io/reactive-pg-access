package io.en4ble.pgaccess.util

import com.fasterxml.jackson.databind.ObjectMapper
import io.en4ble.pgaccess.DatabaseConfig
import io.en4ble.pgaccess.converters.TypedEnumConverter
import io.en4ble.pgaccess.dto.OrderDTO
import io.en4ble.pgaccess.dto.PagingDTO
import io.en4ble.pgaccess.enumerations.SortDirection
import io.en4ble.pgaccess.enumerations.TypedEnum
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import org.jooq.*
import org.slf4j.LoggerFactory
import java.util.*

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object DaoHelperCommon {
    private val LOG by lazy { LoggerFactory.getLogger(DaoHelperCommon::class.java) }
    private val MAPPERS = HashMap<String, ObjectMapper>()

    fun getQueryForLogging(query: Query, config: DatabaseConfig): String {
        return getQueryForLogging(getSql(query, config), JooqHelper.params(query))
    }

    fun getQueryForLogging(sql: String, params: Tuple): String {
        var x = String(sql.toCharArray())
        for (i in 0 until params.size()) {
            val param = params.getValue(i)
            val paramIndex = "$" + (i + 1)
            x = if (param is Number) {
                x.replaceFirst(paramIndex, param.toString())
            } else {
                if (param == null) {
                    x.replaceFirst(paramIndex, "NULL")
                } else {
                    val v = when (param) {
                        is Array<*> -> "'{${param.joinToString()}}'"
                        is Collection<*> -> "'{${param.joinToString()}}'"
                        else -> "'$param'"
                    }
                    x.replaceFirst(paramIndex, v)
                }
            }
        }
        return x
    }

    fun <RECORD : Record> getSortFields(table: Table<RECORD>, order: List<OrderDTO>): List<SortField<*>> {
        return order.filter {
            val exists = table.field(it.field) != null
            if (!exists) {
                LOG.warn("order field ${it.field} does not exist, skipping")
            }
            exists
        }.map {
            val field = table.field(it.field)
            if (it.direction == SortDirection.DESC) field.desc() else field.asc()
        }
    }

    fun addLimit(query: SelectLimitStep<*>, page: PagingDTO): SelectForUpdateStep<*> {
        val numberOfRows = page.numberOfRows
        return if (numberOfRows == null) {
            query.offset(page.offset)
        } else {
            query.limit(page.offset, numberOfRows)
        }
    }

    fun getSql(query: Query, config: DatabaseConfig): String {
        return if (query.bindValues.isEmpty()) {
            query.sql
        } else {
            if (config.preparedStatements) {
                // rewrite placeholders of prepared statements
                // (jOOQ/JDBC uses '?', we need '$' with index)
                val sb = StringBuilder()
                var i = 1
                query.sql.forEach {
                    if (it == '?') {
                        sb.append('$').append(i)
                        i++
                    } else {
                        sb.append(it)
                    }
                }
                sb.toString()
            } else {
                query.sql
            }
        }
    }

    fun uuidList(row: Row, i: Int): List<UUID>? {
        val jsonArray = row.getArrayOfUUIDs(i) ?: return null
        return jsonArray.toList()
    }

    fun stringList(row: Row, i: Int): List<String>? {
        val jsonArray = row.getArrayOfStrings(i) ?: return null
        return jsonArray.toList()
    }

    fun integerList(row: Row, i: Int): List<Int>? {
        val jsonArray = row.getArrayOfIntegers(i) ?: return null
        return jsonArray.toList()
    }

    fun charList(row: Row, i: Int): List<Char>? {
        val jsonArray = row.getArrayOfStrings(i) ?: return null
        return jsonArray.map { it.toCharArray()[0] }.toList()
    }

    fun <O> `object`(row: Row, i: Int, type: Class<O>): O? {
        val string = row.getString(i)
        return if (string == null) null else Json.decodeValue(string, type)
    }

    fun integerNullsafe(row: Row, i: Int): Int {
        val integer = row.getInteger(i)
        return integer?.toInt() ?: -1
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, E : TypedEnum<T>> enumeration(field: Field<E>, row: Row, i: Int): E? {
        val value = row.getValue(i) ?: return null
        return (field.converter as TypedEnumConverter<T, E>).from(value as T)
    }

    fun <DTO> dto(row: Row, i: Int, type: Class<DTO>): DTO? {
        val json = row.getString(i)
        return if (json == null) null else Json.decodeValue(json, type)
    }

    fun <DTO> list(row: Row, i: Int, type: Class<DTO>): List<DTO>? {
        val json = row.getString(i) ?: return null
        val mapper = DatabindCodec.mapper()
        return mapper.readValue<List<DTO>>(
            json, mapper.typeFactory.constructCollectionType(List::class.java, type)
        )
    }

    /**
     * Adds the new condition the a given existing condition with AND.
     * Will return the new condition if the existing condition was null.
     */
    fun getSearchConditionAnd(currentCondition: Condition?, newCondition: Condition): Condition {
        return if (currentCondition != null) {
            currentCondition.and(newCondition)
        } else {
            newCondition
        }
    }

    /**
     * Adds the new condition the a given existing condition with OR.
     * Will return the new condition if the existing condition was null.
     */
    fun getSearchConditionOr(currentCondition: Condition?, newCondition: Condition): Condition {
        return if (currentCondition != null) {
            currentCondition.or(newCondition)
        } else {
            newCondition
        }
    }
}
