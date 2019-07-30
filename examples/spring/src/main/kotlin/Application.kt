package io.en4ble.examples.spring

import io.en4ble.examples.jooq.tables.Conference
import io.en4ble.examples.util.LiquibaseUpdater.updateDatabase
import io.en4ble.pgaccess.DatabaseSettings
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    updateDatabase(Conference.CONFERENCE.schema, DatabaseSettings(schema = "pgaccess"))
    SpringApplication.run(Application::class.java, *args)
}
