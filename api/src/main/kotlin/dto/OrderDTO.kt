package io.en4ble.pgaccess.dto

import io.en4ble.pgaccess.enumerations.SortDirection
import io.swagger.v3.oas.annotations.media.Schema

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
open class OrderDTO constructor(
    @Schema(
        description = "The field to order the results by.",
        defaultValue = "created"
    ) var field: String = "created",
    @Schema(
        description = "The sort order.",
        defaultValue = "DESC"
    )
    var direction: SortDirection = SortDirection.DESC
)

