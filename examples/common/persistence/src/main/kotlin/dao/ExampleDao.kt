package io.en4ble.examples.dao

import io.en4ble.examples.jooq.tables.Example.EXAMPLE
import io.en4ble.examples.jooq.tables.daos.ExampleDaoBase
import io.en4ble.examples.jooq.tables.pojos.ExampleDto
import io.en4ble.pgaccess.DatabaseContext
import io.reactivex.Single
import org.jooq.impl.DSL

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
open class ExampleDao(dbContext: DatabaseContext) : ExampleDaoBase(dbContext) {
    private val query = dsl.select()
        .from(EXAMPLE)
        .orderBy(DSL.rand())
        .limit(1)

    fun rxReadRandom(): Single<ExampleDto> {
        return rxReadOne(query)
    }

    suspend fun readRandom(): ExampleDto {
        return readOne(query)
    }
}
