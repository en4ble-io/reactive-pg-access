package io.en4ble.examples.converters

import io.en4ble.examples.enums.ConferenceState
import io.en4ble.pgaccess.converters.StringEnumConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class ConferenceStateEnumConverter : StringEnumConverter<ConferenceState>(ConferenceState::class.java)
