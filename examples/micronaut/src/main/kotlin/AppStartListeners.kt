package io.en4ble.micronaut.example

import io.en4ble.examples.jooq.tables.Conference
import io.en4ble.examples.util.LiquibaseUpdater.updateDatabase
import io.en4ble.micronaut.example.conf.ExampleConfiguration
import io.en4ble.micronaut.example.conf.ExampleContext
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.runtime.event.annotation.EventListener

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("UNUSED_PARAMETER", "unused")
class AppStartListeners(private val conf: ExampleConfiguration, private val context: ExampleContext) {
    @EventListener
    fun onStartup(event: ServiceStartedEvent) {
        updateDatabase(Conference.CONFERENCE.schema, conf.db!!)
    }

//    /**
//     * Shows how to register beans during runtime / application startup
//     */
//    @EventListener
//    fun onDIStartup(event: StartupEvent) {
//        val beanContext = event.source
//        beanContext.registerSingleton(ConferenceDao(context))
//        beanContext.registerSingleton(ConferenceV1Dao(context))
//        beanContext.refresh()
//    }
}
