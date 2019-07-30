package io.en4ble.pgaccess.mappers

import io.reactiverse.pgclient.PgRowSet
import io.reactiverse.pgclient.Row

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
interface JooqMapper<T> {
    fun toDto(row: Row, offset: Int): T
    fun toDto(row: Row): T {
        return toDto(row, 0)
    }

    fun toList(res: PgRowSet): List<T> {
        return toList(res, 0)
    }

    fun toList(res: PgRowSet, offset: Int): List<T> {
        val list = arrayListOf<T>()
        res.forEach { array -> list.add(toDto(array, offset)) }
        return list
    }

    fun getValueMap(dto: Any): Map<org.jooq.Field<*>, *>
}
