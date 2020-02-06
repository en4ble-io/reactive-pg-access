package io.en4ble.examples.spring.conf

import io.en4ble.pgaccess.DatabaseContext
import io.en4ble.pgaccess.DatabaseConfig
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class ExampleContext : DatabaseContext(DatabaseConfig(schema = "pgaccess"))
