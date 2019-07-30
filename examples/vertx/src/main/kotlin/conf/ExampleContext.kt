package io.en4ble.pgaccess.example.conf

import io.en4ble.examples.jooq.tables.Conference
import io.en4ble.pgaccess.DatabaseContext
import io.en4ble.pgaccess.DatabaseSettings
import io.vertx.reactivex.core.Vertx

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
class ExampleContext(vertx: Vertx) : DatabaseContext(
    vertx, DatabaseSettings(schema = Conference.CONFERENCE.schema.name)
)
