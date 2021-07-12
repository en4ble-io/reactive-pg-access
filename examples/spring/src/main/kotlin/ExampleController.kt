package io.en4ble.examples.spring

import io.en4ble.examples.jooq.tables.pojos.ExampleDto
import io.en4ble.examples.spring.dao.SpringExampleDao
import io.en4ble.examples.spring.services.SpringExampleService
import kotlinx.coroutines.rx2.rxSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.adapter.rxjava.RxJava2Adapter
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
    fun create(): Mono<ExampleDto> {
        return RxJava2Adapter.singleToMono(rxSingle { exampleService.create() })
    }

    @GetMapping("/randomRx")
    fun example(): Mono<ExampleDto> {
        return RxJava2Adapter.singleToMono(exampleDao.rxReadRandom())
    }

    @GetMapping("/randomCr")
    fun example2(): Mono<ExampleDto> {
        return RxJava2Adapter.singleToMono(rxSingle { exampleDao.readRandom() })
    }

    @PutMapping("/")
    fun update(): Mono<ExampleDto> {
        return RxJava2Adapter.singleToMono(rxSingle { exampleService.update() })
    }
}
