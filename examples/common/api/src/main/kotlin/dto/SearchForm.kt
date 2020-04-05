package io.en4ble.examples.dto

import io.en4ble.pgaccess.dto.PageDTO
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
//@Introspected
data class SearchForm(
    @Schema(name = "find all conferences with a name like the given one") var name: String? = null,
    @Schema(name = "find all conferences with a start date greater or equal the given date") var start: LocalDate? = null,
    var page: PageDTO<UUID> = PageDTO()
)
