package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotNull

/**
 * Data object used to send a circle.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class CircleDTO(@get:NotNull var centre: PointDTO? = null, @get:NotNull var radius: String? = null) : Serializable
