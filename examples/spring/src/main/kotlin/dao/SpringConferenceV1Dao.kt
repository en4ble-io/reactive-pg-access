package io.en4ble.examples.spring.dao

import io.en4ble.examples.dao.ConferenceV1Dao
import io.en4ble.examples.spring.conf.ExampleContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Component
class SpringConferenceV1Dao(@Autowired context: ExampleContext) : ConferenceV1Dao(context)

