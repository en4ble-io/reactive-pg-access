package io.en4ble.micronaut.example.conf

import io.en4ble.pgaccess.DatabaseConfig
import io.micronaut.context.annotation.ConfigurationProperties

/**
 * @author Mark Hofmann (mark@en4ble.io)
 */
@ConfigurationProperties("en4ble")
class ExampleConfiguration {
    var db: DbSettings? = null

    @ConfigurationProperties("db")
    class DbSettings : DatabaseConfig()
}
