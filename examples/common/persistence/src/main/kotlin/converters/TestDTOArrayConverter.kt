package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOArrayConverter :
    AbstractConverter<Array<JsonObject>, Array<TestDTO>>(Array<JsonObject>::class.java, Array<TestDTO>::class.java) {
    override fun from(databaseObject: Array<JsonObject>?): Array<TestDTO>? {
        if (databaseObject == null) return null
        return databaseObject.map { Json.decodeValue(it.toString(), TestDTO::class.java) }
            .toTypedArray()
    }

    override fun to(userObject: Array<TestDTO>?): Array<JsonObject>? {
        if (userObject == null) return null
        return userObject.map { JsonObject(Json.encodeToBuffer(it)) }.toTypedArray()
    }
}
