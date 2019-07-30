package io.en4ble.pgaccess.dto

import java.io.Serializable

/**
 * Data object used to send a line.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class LineDTO(var a: Double = 0.0, var b: Double = 0.0, var c: Double = 0.0) : Serializable
