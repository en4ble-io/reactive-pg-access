package io.en4ble.micronaut.example.conf

import io.en4ble.examples.dao.ConferenceDao
import io.en4ble.examples.dao.ConferenceV1Dao
import io.micronaut.context.annotation.Factory
import javax.inject.Singleton

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Factory
class ExampleFactory() {
    @Singleton
    fun conferenceDao(context: ExampleContext) = ConferenceDao(context)

    @Singleton
    fun conferenceV1Dao(context: ExampleContext) = ConferenceV1Dao(context)
}
