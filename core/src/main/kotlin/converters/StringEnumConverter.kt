package io.en4ble.pgaccess.converters

import io.en4ble.pgaccess.enumerations.StringEnum

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
abstract class StringEnumConverter<E : StringEnum> constructor(type: Class<E>) :
    TypedEnumConverter<String, E>(String::class.java, type)
