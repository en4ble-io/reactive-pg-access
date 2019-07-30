package io.en4ble.examples.dao

import io.en4ble.examples.dto.SearchForm
import io.en4ble.examples.jooq.Tables.CONFERENCE_V1
import io.en4ble.examples.jooq.tables.daos.ConferenceV1DaoBase
import io.en4ble.examples.jooq.tables.pojos.ConferenceV1Dto
import io.en4ble.pgaccess.DatabaseContext
import io.reactivex.Single
import org.jooq.impl.DSL

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
open class ConferenceV1Dao(exampleContext: DatabaseContext) : ConferenceV1DaoBase(exampleContext) {
    fun findByStartAfter(form: SearchForm) =
        rxRead(CONFERENCE_V1.START_DATE.ge(form.start))

    fun findByNameLike(form: SearchForm) =
        rxRead(
            CONFERENCE_V1.NAME.like("%" + form.name + "%")
        )

    fun loadRandomConference(): Single<ConferenceV1Dto> {
        return rxReadOne(
            dsl.select()
                .from(CONFERENCE_V1)
                .orderBy(DSL.rand())
                .limit(1)
        )
    }
}
