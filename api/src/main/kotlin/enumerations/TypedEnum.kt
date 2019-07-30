package io.en4ble.pgaccess.enumerations

/**
 * All enums that are stored in the database need to implement this interface.
 *
 *
 * For details see io.en4ble.common.jooq.converters.TypedEnumConverter
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
interface TypedEnum<T> {

    /**
     * The key that is used to store the enum value. <br></br>
     * Don't ever change the value of the key once it has been used in the database!
     *
     * @return The key that is used to store the enum value.
     */
    val key: T
}
