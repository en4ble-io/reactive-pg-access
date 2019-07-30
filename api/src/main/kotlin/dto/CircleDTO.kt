package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotNull

/**
 * Data object used to send a circle.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class CircleDTO(@NotNull var centre: PointDTO? = null, @NotNull var radius: Double = 0.0) : Serializable
