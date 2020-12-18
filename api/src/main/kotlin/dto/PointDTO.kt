package io.en4ble.pgaccess.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/**
 * Data object used to send a location.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
@Schema(name = "Point")
data class PointDTO(
    @JsonProperty(required = true)
    var lng: Double = 0.0,
    @JsonProperty(required = true)
    var lat: Double = 0.0
) : Serializable {

    /**
     * Convenience method to work with the x coordinate = longitude
     */
    var x
        @Hidden
        @JsonIgnore
        get() = lng
        @JsonIgnore
        @Hidden
        set(value) {
            lng = value
        }

    /**
     * Convenience method to work with the y coordinate = latitude
     */
    var y
        @Hidden
        @JsonIgnore
        get() = lat
        @Hidden
        @JsonIgnore
        set(value) {
            lat = value
        }

    /**
     * This value is used by jOOQ when inlining parameters.
     * Don't touch it!
     */
    override fun toString(): String {
        return "($x,$y)"
    }
}
