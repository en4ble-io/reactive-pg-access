package io.en4ble.micronaut.example

import io.en4ble.examples.dto.SearchForm
import io.en4ble.examples.jooq.tables.pojos.ConferenceDto
import io.en4ble.examples.jooq.tables.pojos.ConferenceV1Dto
import io.en4ble.micronaut.example.services.MnConferenceService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Status
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.rx2.rxSingle
import java.util.UUID
import javax.validation.Valid

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
@Validated
@ApiResponses(
    ApiResponse(responseCode = "500", description = "Fatal error - the request could not be processed")
)
@Controller("/conferences", consumes = ["application/json"], produces = ["application/json"])
class ConferenceController(private val service: MnConferenceService) {
    @Post("/")
    @Status(HttpStatus.CREATED)
    fun createConf(@Valid @Body conference: ConferenceDto): Single<ConferenceV1Dto> {
        return service.createConference(conference)
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

    @Get("/{id}")
    fun getConf(id: UUID): Single<ConferenceV1Dto> {
        return GlobalScope.rxSingle { service.getConference(id) }
    }

    @Get("/random")
    fun randomConf(): Single<ConferenceV1Dto> {
        return service.randomConference()
    }

    @Operation(
        summary = "Searches for matching conferences",
        description = "If all parameters are null, all conferences will be returned."
    )
    @Post("/search")
    fun search(@Body form: SearchForm): Single<List<ConferenceV1Dto>> {
        return service.search(form)
    }

    @Operation(
        summary = "Updates the conference with the given id.",
        description = "Omitted / null attributes will not be changed."
    )
    @Put("/{id}")
    fun updateConf(id: UUID, @Body form: ConferenceDto): Single<ConferenceV1Dto> {
        return service.updateConference(id, form)
    }

    @Operation(
        summary = "Deletes the conference with the given id by changing the state to DELETED"
    )
    @Delete("/{id}")
    fun deleteConf(id: UUID) {
        service.deleteConference(id).map { "Deleted" }
    }
}
