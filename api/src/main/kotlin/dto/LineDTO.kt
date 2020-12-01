package io.en4ble.pgaccess.dto

import java.io.Serializable

/**
 * Data object used to send a line.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class LineDTO(var a: String? = null, var b: String? = null, var c: String? = null) : Serializable {
    /**
     * This value is used by jOOQ when inlining parameters.
     * Don't touch it!
     */
    override fun toString(): String {
        return listOf(a, b, c).joinToString(",")
    }

}
