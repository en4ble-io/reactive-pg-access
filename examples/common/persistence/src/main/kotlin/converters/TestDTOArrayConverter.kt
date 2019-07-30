package io.en4ble.examples.converters

import io.en4ble.examples.dto.TestDTO
import io.reactiverse.pgclient.data.Json
import org.jooq.impl.AbstractConverter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class TestDTOArrayConverter :
    AbstractConverter<Array<Json>, Array<TestDTO>>(Array<Json>::class.java, Array<TestDTO>::class.java) {
    override fun from(databaseObject: Array<Json>?): Array<TestDTO>? {
        if (databaseObject == null) return null
        return databaseObject.map { io.vertx.core.json.Json.decodeValue(it.toString(), TestDTO::class.java) }
            .toTypedArray()
    }

    override fun to(userObject: Array<TestDTO>?): Array<Json>? {
        if (userObject == null) return null
        return userObject.map { Json.create(io.vertx.core.json.Json.encode(it)) }.toTypedArray()
    }
}
