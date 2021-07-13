package io.en4ble.examples.spring

import io.en4ble.examples.jooq.tables.pojos.ExampleDto
import io.en4ble.examples.spring.dao.SpringExampleDao
import io.en4ble.examples.spring.services.SpringExampleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.adapter.rxjava.RxJava2Adapter.singleToMono
import reactor.core.publisher.Mono

@Suppress("unused")
@RestController
@RequestMapping("/examples")
class ExampleController {

    @Autowired
    private lateinit var exampleDao: SpringExampleDao

    @Autowired
    private lateinit var exampleService: SpringExampleService

    @PostMapping("/")
    suspend fun create(): ExampleDto {
        return exampleService.create()
    }

    @GetMapping("/randomRx")
    fun example(): Mono<ExampleDto> {
        return singleToMono(exampleDao.rxReadRandom())
    }

    @GetMapping("/randomCr")
    suspend fun example2(): ExampleDto {
        return exampleDao.readRandom()
    }

    @PutMapping("/")
    suspend fun update(): ExampleDto {
        return exampleService.update()
    }
}
