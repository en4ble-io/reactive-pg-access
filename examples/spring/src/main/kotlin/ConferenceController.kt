package io.en4ble.examples.spring

import io.en4ble.examples.dto.SearchForm
import io.en4ble.examples.jooq.tables.pojos.ConferenceDto
import io.en4ble.examples.jooq.tables.pojos.ConferenceV1Dto
import io.en4ble.examples.spring.services.SpringConferenceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.rx2.rxSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.adapter.rxjava.RxJava2Adapter.singleToMono
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
@ApiResponses(
    ApiResponse(responseCode = "500", description = "Fatal error - the request could not be processed")
)
@RestController
@RequestMapping("/conferences")
class ConferenceController {
    @Autowired
    private lateinit var service: SpringConferenceService

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createConf(@Valid @RequestBody conference: ConferenceDto): Mono<ConferenceV1Dto> {
        return singleToMono(service.createConference(conference))
    }

    @Operation(
        summary = "Gets the conference with the given id"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "The requested conference.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ConferenceV1Dto::class)
            )]
        ),
        ApiResponse(responseCode = "404", description = "The conference does not exist.")
    )
    @GetMapping("/{id}")
    fun getConf(id: UUID): Mono<ConferenceV1Dto> {
        return singleToMono(rxSingle { service.getConference(id) })
    }

    @GetMapping("/random")
    fun randomConf(): Mono<ConferenceV1Dto> {
        return singleToMono(service.randomConference())
    }

    @Operation(
        summary = "Searches for matching conferences",
        description = "If all parameters are null, all conferences will be returned."
    )
    @PostMapping("/search")
    fun search(@RequestBody form: SearchForm): Mono<List<ConferenceV1Dto>> {
        return singleToMono(service.search(form))
    }

    @Operation(
        summary = "Updates the conference with the given id.",
        description = "Omitted/null attributes will not be changed."
    )
    @PutMapping("/{id}")
    fun updateConf(id: UUID, @RequestBody form: ConferenceDto): Mono<ConferenceV1Dto> {
        return singleToMono(service.updateConference(id, form))
    }

    @Operation(
        summary = "Deleted the conference with the given id by changing the state to DELETED"
    )
    @DeleteMapping("/{id}")
    fun deleteConf(id: UUID): Mono<String> {
        return singleToMono(service.deleteConference(id).map { "Deleted" })
    }
}
