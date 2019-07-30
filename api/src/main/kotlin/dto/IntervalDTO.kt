package io.en4ble.pgaccess.dto

import java.io.Serializable

/**
 * Data object used to send an interval.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class IntervalDTO(
    var years: Int = 0,
    var months: Int = 0,
    var days: Int = 0,
    var hours: Int = 0,
    var minutes: Int = 0,
    var seconds: Int = 0,
    var microseconds: Int = 0
) : Serializable
