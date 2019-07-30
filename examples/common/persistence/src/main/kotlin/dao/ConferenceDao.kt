package io.en4ble.examples.dao

import io.en4ble.examples.enums.ConferenceState
import io.en4ble.examples.jooq.tables.Conference
import io.en4ble.examples.jooq.tables.Conference.CONFERENCE
import io.en4ble.examples.jooq.tables.daos.ConferenceDaoBase
import io.en4ble.examples.jooq.tables.pojos.ConferenceDto
import io.en4ble.pgaccess.DatabaseContext
import io.reactivex.Single
import java.util.UUID

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
open class ConferenceDao(exampleContext: DatabaseContext) : ConferenceDaoBase(exampleContext) {

    fun update(
        id: UUID,
        form: ConferenceDto
    ): Single<ConferenceDto> {
        return rxUpdateReturningOne(form, Conference.CONFERENCE.ID.eq(id))
        // if you want to allow update of some fields only, you could use the approach below
        // or mark fields that should not be updated by the user as "readOnly" in the table definition
        // see the README of reactive-pg-access for more information
//        return rxUpdateReturning(
//            Pair(CONFERENCE.NAME, form.name),
//            Pair(CONFERENCE.ABOUT, form.about),
//            Pair(CONFERENCE.START_DATE, form.startDate),
//            Pair(CONFERENCE.END_DATE, form.endDate),
//            Pair(CONFERENCE.LOCATION, form.location),
//            CONFERENCE.ID.eq(id)
//        ).first()
    }

    fun setState(id: UUID, state: ConferenceState): Single<Int> {
        return rxUpdate(
            Pair(CONFERENCE.STATE, state),
            CONFERENCE.ID.eq(id)
        )
    }
}
