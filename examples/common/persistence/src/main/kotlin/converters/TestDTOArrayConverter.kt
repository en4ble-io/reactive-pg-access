package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.vertx.core.json.Json
import org.jooq.JSON
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOArrayConverter :
    AbstractConverter<Array<JSON>, Array<TestDTO>>(Array<JSON>::class.java, Array<TestDTO>::class.java) {
    override fun from(databaseObject: Array<JSON>?): Array<TestDTO>? {
        if (databaseObject == null) return null
        return databaseObject.map { Json.decodeValue(it.data(), TestDTO::class.java) }
            .toTypedArray()
    }

    override fun to(userObject: Array<TestDTO>?): Array<JSON>? {
        if (userObject == null) return null
        return userObject.map { JSON.valueOf(Json.encode(it)) }.toTypedArray()
    }
}
