package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.vertx.core.json.Json
import org.jooq.JSON
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOConverter : AbstractConverter<JSON, TestDTO>(JSON::class.java, TestDTO::class.java) {
    override fun from(databaseObject: JSON?): TestDTO? {
        if (databaseObject == null) return null
        return Json.decodeValue(databaseObject.data(), TestDTO::class.java)
    }

    override fun to(userObject: TestDTO?): JSON? {
        if (userObject == null) return null
        return JSON.valueOf(Json.encode(userObject))
    }
}
