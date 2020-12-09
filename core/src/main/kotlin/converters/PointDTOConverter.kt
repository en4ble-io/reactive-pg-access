package io.en4ble.pgaccess.converters

import io.en4ble.pgaccess.dto.PointDTO
import io.vertx.pgclient.data.Point
import org.jooq.Converter

/** @author Mark Hofmann (mark@en4ble.io)
 */
class PointDTOConverter : Converter<Any, PointDTO> {

    override fun from(`object`: Any?): PointDTO? {
        if (`object` == null) {
            return null
        }

        val point = `object` as Point
        return PointDTO(point.x, point.y)
    }

    override fun to(position: PointDTO?): Any? {
        if (position == null) {
            return null
        }

        return Point(position.x, position.y)
    }

    override fun fromType(): Class<Any> {
        return Any::class.java
    }

    override fun toType(): Class<PointDTO> {
        return PointDTO::class.java
    }
}
