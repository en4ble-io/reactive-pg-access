package io.en4ble.examples.spring.conf

import io.en4ble.pgaccess.DatabaseConfig
import io.en4ble.pgaccess.SingleDatabaseContext
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class ExampleContext : SingleDatabaseContext(DatabaseConfig(schema = "pgaccess"))
