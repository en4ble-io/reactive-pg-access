package io.en4ble.micronaut.example

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
    info = Info(
        title = "Example",
        version = "0.1",
        description = "Mini API implementing the random conference example of Micronaut",
        license = License(name = "Apache 2.0", url = "http://en4ble.io"),
        contact = Contact(url = "https://en4ble.io", name = "Mark Hofmann", email = "mark@en4ble.io")
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.en4ble.micronaut.example")
            .mainClass(Application.javaClass)
            .start()
    }
}
