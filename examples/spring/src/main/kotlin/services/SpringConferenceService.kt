package io.en4ble.examples.spring.services

import io.en4ble.examples.services.ConferenceService
import io.en4ble.examples.spring.dao.SpringConferenceDao
import io.en4ble.examples.spring.dao.SpringConferenceV1Dao
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class SpringConferenceService(
    conferenceDao: SpringConferenceDao,
    conferenceV1Dao: SpringConferenceV1Dao
) : ConferenceService(conferenceDao, conferenceV1Dao)
