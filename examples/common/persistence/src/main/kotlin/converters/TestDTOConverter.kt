package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.reactiverse.pgclient.data.Json
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOConverter : AbstractConverter<Json, TestDTO>(Json::class.java, TestDTO::class.java) {
    override fun from(databaseObject: Json?): TestDTO? {
        if (databaseObject == null) return null
        return io.vertx.core.json.Json.decodeValue(databaseObject.toString(), TestDTO::class.java)
    }

    override fun to(userObject: TestDTO?): Json? {
        if (userObject == null) return null
        return Json.create(io.vertx.core.json.Json.encode(userObject))
    }
}
