package io.en4ble.pgaccess.converters

import io.en4ble.pgaccess.enumerations.IntEnum

/**
 *
 * NOTE: all enum converters must be named "{Enum name}EnumConverter" for the code generation to recognize the associated columns as enums.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
abstract class IntEnumConverter<E : IntEnum> constructor(type: Class<E>) :
    TypedEnumConverter<Int, E>(Int::class.java, type)
