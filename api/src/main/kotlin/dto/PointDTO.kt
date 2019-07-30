package io.en4ble.pgaccess.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.Hidden
import java.io.Serializable
import javax.validation.constraints.NotEmpty

/**
 * Data object used to send a location.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
data class PointDTO(@NotEmpty var x: String, @NotEmpty var y: String) : Serializable {

    constructor() : this("0.0", "0.0")

    /**
     * Convenience method to get the x coordinate = longitude
     * @return the x value
     */
    @Hidden
    @JsonIgnore
    fun getLng(): String = x

    /**
     * Convenience method to get the y coordinate = latitude
     * @return the y value
     */
    @Hidden
    @JsonIgnore
    val getLat: String = y
}
