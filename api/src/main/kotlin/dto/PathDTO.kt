package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotEmpty

/**
 * Data object used to send a path.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class PathDTO(var isOpen: Boolean, @NotEmpty var points: List<PointDTO>? = null) : Serializable {
    /**
     * This value is used by jOOQ when inlining parameters.
     * Don't touch it!
     */
    override fun toString(): String {
        if (points == null) {
            return ""
        }
        val (prefix, suffix) = if (isOpen) {
            "[" to "]"
        } else {
            "(" to ")"
        }
        return prefix + points?.joinToString(",") + suffix
    }
}
