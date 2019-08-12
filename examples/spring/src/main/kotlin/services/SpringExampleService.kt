package io.en4ble.examples.spring.services

import io.en4ble.examples.services.ExampleService
import io.en4ble.examples.spring.dao.SpringExampleDao
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class SpringExampleService(
    exampleDao: SpringExampleDao
) : ExampleService(exampleDao)
