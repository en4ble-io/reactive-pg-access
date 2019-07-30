@file:Suppress("unused")

package io.en4ble.pgaccess.converters

import io.en4ble.pgaccess.enumerations.TypedEnum
import org.jooq.impl.AbstractConverter
import java.util.LinkedHashMap

/**
 * Base converter class for storing Enums in the database.
 *
 * @see TypedEnum
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
abstract class TypedEnumConverter<T, E : TypedEnum<T>>(fromType: Class<T>, toType: Class<E>) :
    AbstractConverter<T, E>(fromType, toType) {

    private val lookup: MutableMap<T?, E>

    init {
        this.lookup = LinkedHashMap()
        for (u in toType.enumConstants) {
            this.lookup[to(u)] = u
        }
    }

    override fun toString(): String {
        return "TypedEnumConverter [ " + fromType().name + " -> " + toType().name + " ]"
    }

    override fun from(databaseObject: T): E? {
        return lookup[databaseObject]
    }

    final override fun to(userObject: E?): T? {
        return userObject?.key
    }
}
