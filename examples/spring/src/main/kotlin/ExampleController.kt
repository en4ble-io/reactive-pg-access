package io.en4ble.examples.spring

import io.en4ble.examples.spring.dao.SpringExampleDao
import io.en4ble.examples.jooq.tables.pojos.ExampleDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Mono

@Suppress("unused")
@RestController
class ExampleController {

    @Autowired
    private lateinit var exampleDao: SpringExampleDao

    @GetMapping("/example")
    fun example(): Mono<ExampleDto> {
        return RxJava2Adapter.singleToMono(exampleDao.rxReadRandom())
    }
}
