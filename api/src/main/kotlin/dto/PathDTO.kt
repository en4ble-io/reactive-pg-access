package io.en4ble.pgaccess.dto

import java.io.Serializable
import javax.validation.constraints.NotEmpty

/**
 * Data object used to send a path.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class PathDTO(var isOpen: Boolean, @NotEmpty var points: List<PointDTO>?) : Serializable
