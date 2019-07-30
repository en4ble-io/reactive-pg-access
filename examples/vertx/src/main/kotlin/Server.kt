package io.en4ble.pgaccess.example

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.en4ble.examples.dao.ConferenceDao
import io.en4ble.examples.dao.ConferenceV1Dao
import io.en4ble.examples.dao.ExampleDao
import io.en4ble.examples.jooq.tables.Conference
import io.en4ble.examples.services.ConferenceService
import io.en4ble.examples.services.ExampleService
import io.en4ble.examples.util.LiquibaseUpdater
import io.en4ble.pgaccess.DatabaseSettings
import io.en4ble.pgaccess.example.conf.ExampleContext
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.reactivex.core.AbstractVerticle
import org.slf4j.LoggerFactory
import javax.validation.Validation

fun main() {
    LiquibaseUpdater.updateDatabase(
        Conference.CONFERENCE.schema,
        DatabaseSettings(schema = "pgaccess")
    )
    Vertx.vertx().deployVerticle(Server())
}

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
class Server : AbstractVerticle() {
    private val LOG by lazy { LoggerFactory.getLogger(Server::class.java) }
    private lateinit var conferenceService: ConferenceService
    private lateinit var exampleService: ExampleService

    override fun start() {
        val dbContext = ExampleContext(vertx)
        val validator = Validation.byDefaultProvider().configure()
            .buildValidatorFactory().validator
        conferenceService = ConferenceService(ConferenceDao(dbContext), ConferenceV1Dao(dbContext))
        exampleService = ExampleService(ExampleDao(dbContext))
        Json.mapper.registerModule(JavaTimeModule())
        Json.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        vertx.createHttpServer()
            .requestHandler(Router(Router.router(vertx.delegate), validator, exampleService, conferenceService))
            .listen(8080) { result ->
                if (result.succeeded()) {
                    LOG.info("HTTP server running on port 8080")
                } else {
                    LOG.error("error starting http server", result.cause())
                    System.exit(-1)
                }
            }
    }
}

