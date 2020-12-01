package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotNull

/**
 * Data object used to send a box.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class BoxDTO(@get:NotNull var ne: PointDTO? = null, @get:NotNull var sw: PointDTO? = null) :
    Serializable {
    /**
     * This value is used by jOOQ when inlining parameters.
     * Don't touch it!
     */
    override fun toString(): String {
        return listOf(ne, sw).joinToString(",")
    }
}
