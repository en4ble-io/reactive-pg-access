package io.en4ble.pgaccess.example

import io.en4ble.examples.dto.SearchForm
import io.en4ble.examples.jooq.tables.pojos.ConferenceDto
import io.en4ble.examples.services.ConferenceService
import io.en4ble.examples.services.ExampleService
import javax.validation.Validator

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
class Router(
    delegate: io.vertx.ext.web.Router,
    validator: Validator,
    private val exampleService: ExampleService,
    private val conferenceService: ConferenceService

) : RouterBase(delegate, validator) {

    init {
        post("/examples").cr { exampleService.create() }
        put("/examples").cr { exampleService.update() }
        put("/examples2").cr { exampleService.update2() }

        post("/conferences").rx { conferenceService.createConference(getForm(it, ConferenceDto::class.java)) }
        get("/conferences/random").rx { conferenceService.randomConference() }
        get("/conferences/:id").rx { conferenceService.getConference(getId(it)) }
        post("/conferences/search").rx {
            conferenceService.search(getForm(it, SearchForm::class.java))
        }
        put("/conferences/:id").rx {
            conferenceService.updateConference(
                getId(it),
                getForm(it, ConferenceDto::class.java)
            )
        }
        delete("/conferences/:id").rx { conferenceService.deleteConference(getId(it)).map { "Deleted" } }
    }
}
