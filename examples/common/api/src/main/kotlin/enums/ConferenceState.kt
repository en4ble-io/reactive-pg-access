package io.en4ble.examples.enums

import io.en4ble.pgaccess.enumerations.StringEnum

/**
 * @author Mark Hofmann (mark@en4ble.io)
 */
enum class ConferenceState constructor(override var key: String) : StringEnum {
    NEW("N"),
    ACTIVE("A"),
    DELETED("D")
}
