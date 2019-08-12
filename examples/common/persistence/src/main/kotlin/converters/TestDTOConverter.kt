package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.vertx.core.json.JsonObject
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOConverter : AbstractConverter<JsonObject, TestDTO>(JsonObject::class.java, TestDTO::class.java) {
    override fun from(databaseObject: JsonObject?): TestDTO? {
        if (databaseObject == null) return null
        return io.vertx.core.json.Json.decodeValue(databaseObject.toString(), TestDTO::class.java)
    }

    override fun to(userObject: TestDTO?): JsonObject? {
        if (userObject == null) return null
        return JsonObject(io.vertx.core.json.Json.encodeToBuffer(userObject))
    }
}
