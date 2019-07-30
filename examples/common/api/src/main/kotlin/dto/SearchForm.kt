package io.en4ble.examples.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
//@Introspected
data class SearchForm(
    @Schema(name = "find all conferences with a name like the given one") var name: String? = null,
    @Schema(name = "find all conferences with a start date greater or equal the given date") var start: LocalDate? = null,
    var page: PageDTO = PageDTO()
)
