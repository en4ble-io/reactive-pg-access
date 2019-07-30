package io.en4ble.micronaut.example.conf

import io.en4ble.examples.jooq.tables.Conference.CONFERENCE
import io.en4ble.pgaccess.DatabaseContext
import javax.inject.Singleton

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Singleton
class ExampleContext(conf: ExampleConfiguration) : DatabaseContext(null, conf.db!!, CONFERENCE.schema)
