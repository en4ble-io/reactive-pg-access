package io.en4ble.pgaccess.dto

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
class PagingDTO constructor(var offset: Int = 0, var orderBy: List<OrderDTO>? = null) {
    var numberOfRows: Int? = null

    constructor() : this(0, 10)

    constructor(orderBy: OrderDTO) : this() {
        this.orderBy = listOf(orderBy)
    }

    constructor(orderBy: List<OrderDTO>) : this() {
        this.orderBy = orderBy
    }

    constructor(page: Int, pageSize: Int) : this(page * pageSize) {
        this.numberOfRows = pageSize
    }

    constructor(page: Int, pageSize: Int, orderBy: OrderDTO) : this(orderBy) {
        this.numberOfRows = pageSize
        this.offset = page * pageSize
    }

    constructor(page: Int, pageSize: Int, order: List<OrderDTO>) : this(page, pageSize) {
        this.numberOfRows = pageSize
        this.orderBy = order
    }
}
