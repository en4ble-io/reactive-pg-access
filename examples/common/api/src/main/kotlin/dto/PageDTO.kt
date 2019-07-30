package io.en4ble.examples.dto

import io.en4ble.pgaccess.enumerations.SortDirection
import java.time.LocalDate
import java.util.UUID

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
data class PageDTO(
    val id: UUID? = null,
    val startDate: LocalDate? = null,
    val direction: SortDirection = SortDirection.ASC,
    val size: Int = 5
)
