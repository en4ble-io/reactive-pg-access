package io.en4ble.examples.spring.dao

import io.en4ble.examples.dao.ConferenceDao
import io.en4ble.examples.spring.conf.ExampleContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class SpringConferenceDao(@Autowired context: ExampleContext) : ConferenceDao(context)

