package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotEmpty

/**
 * Data object used to send a polygon.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class PolygonDTO(@NotEmpty var points: List<PointDTO>?) : Serializable
