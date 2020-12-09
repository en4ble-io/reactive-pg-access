package io.en4ble.pgaccess.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.Hidden
import java.io.Serializable

/**
 * Data object used to send a location.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
data class PointDTO(var x: Double = 0.0, var y: Double = 0.0) : Serializable {

    /**
     * Convenience method to work with the x coordinate = longitude
     * @return the x value
     */
    var lng: Double
        @Hidden
        @JsonIgnore
        get() = x
        set(value) {
            x = value
        }

    /**
     * Convenience method to work with the y coordinate = latitude
     * @return the y value
     */
    var lat: Double
        @Hidden
        @JsonIgnore
        get() = y
        set(value) {
            y = value
        }

    /**
     * This value is used by jOOQ when inlining parameters.
     * Don't touch it!
     */
    override fun toString(): String {
        return "($x,$y)"
    }
}
