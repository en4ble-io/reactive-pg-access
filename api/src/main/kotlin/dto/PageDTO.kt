package io.en4ble.pgaccess.dto

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.Valid

/**
 * Contains information on how to retrieve the next requested page for the current search.
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Schema(description = "Contains information on how to retrieve the next requested page for the current search.")
open class PageDTO<ID> {
    /** The id of the last displayed element. Used for efficient queries using keyset pagination.  */
    @Schema(description = "The id of the last displayed element. Used for efficient queries using keyset pagination.")
    var baseId: ID? = null

    @Schema(description = "The String representation of the value of the last displayed element that is to be used for ordering. Must correspond to the field of the orderBy attribute.")
    var baseValue: String? = null

    /** The maximum number of results that should be returned. Default: 10  */
    @Schema(defaultValue = "10", description = "The maximum number of results that should be returned.")
    var size = 10

    @Valid
    var orderBy: OrderDTO = OrderDTO()
}
