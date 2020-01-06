package io.en4ble.pgaccess.dto

import io.en4ble.pgaccess.enumerations.SortDirection

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
class OrderDTO constructor(val field: String = "created", val direction: SortDirection = SortDirection.DESC)

