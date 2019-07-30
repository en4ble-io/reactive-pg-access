package io.en4ble.micronaut.example.services

import io.en4ble.examples.dao.ConferenceDao
import io.en4ble.examples.dao.ConferenceV1Dao
import io.en4ble.examples.services.ConferenceService
import javax.inject.Singleton

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Singleton
class MnConferenceService(
    conferenceDao: ConferenceDao,
    conferenceV1Dao: ConferenceV1Dao
) : ConferenceService(conferenceDao, conferenceV1Dao)
