package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotNull

/**
 * Data object used to send a line segment.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class LineSegmentDTO(@NotNull var a: PointDTO? = null, @NotNull var b: PointDTO? = null) : Serializable
