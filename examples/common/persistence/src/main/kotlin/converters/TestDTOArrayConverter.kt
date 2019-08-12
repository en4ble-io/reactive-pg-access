package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOArrayConverter :
    AbstractConverter<JsonArray, Array<TestDTO>>(JsonArray::class.java, Array<TestDTO>::class.java) {
    override fun from(databaseObject: JsonArray?): Array<TestDTO>? {
        if (databaseObject == null) return null
        return databaseObject.list.map { Json.decodeValue(it.toString(), TestDTO::class.java) }
            .toTypedArray()
    }

    override fun to(userObject: Array<TestDTO>?): JsonArray? {
        if (userObject == null) return null
        return JsonArray(userObject.map { JsonObject(Json.encodeToBuffer(it)) })
    }
}
