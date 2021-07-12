package io.en4ble.examples.services

import io.en4ble.examples.dao.ConferenceDao
import io.en4ble.examples.dao.ConferenceV1Dao
import io.en4ble.examples.dto.SearchForm
import io.en4ble.examples.enums.ConferenceState
import io.en4ble.examples.jooq.tables.pojos.ConferenceDto
import io.en4ble.examples.jooq.tables.pojos.ConferenceV1Dto
import io.reactivex.Single
import java.util.*

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
open class ConferenceService(
    protected val conferenceDao: ConferenceDao,
    protected val conferenceV1Dao: ConferenceV1Dao
) {
    fun createConference(form: ConferenceDto): Single<ConferenceV1Dto> {
        form.id = UUID.randomUUID()
        return conferenceDao.rxCreateReturning(form, null)
            .map { toConferenceV1(it) }
    }

    suspend fun getConference(id: UUID): ConferenceV1Dto {
        return conferenceV1Dao.readOneById(id)
    }

    fun randomConference(): Single<ConferenceV1Dto> {
        return conferenceV1Dao.loadRandomConference()
    }

    fun search(form: SearchForm): Single<List<ConferenceV1Dto>> {
        return if (!form.name.isNullOrEmpty()) {
            conferenceV1Dao.findByNameLike(form)
        } else if (form.start != null) {
            conferenceV1Dao.findByStartAfter(form)
        } else {
            val page = form.page
            return conferenceV1Dao.rxReadPage(page)
        }
    }

    fun updateConference(id: UUID, form: ConferenceDto): Single<ConferenceV1Dto> {
        return conferenceDao.update(id, form)
            .map { toConferenceV1(it) }
    }

    fun deleteConference(id: UUID): Single<Int> {
        return conferenceDao.setState(id, ConferenceState.DELETED)
    }

    private fun toConferenceV1(it: ConferenceDto) =
        ConferenceV1Dto(it.id, it.name, it.about, it.startDate, it.endDate, it.location, it.state)
}
