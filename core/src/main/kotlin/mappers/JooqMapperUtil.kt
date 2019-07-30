package io.en4ble.pgaccess.mappers

import io.vertx.core.json.JsonArray
import java.util.UUID

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
object JooqMapperUtil {
//  private val dateFormatter by lazy {
//    val df = SimpleDateFormat(ApiConstants.JSON_DATETIME, Locale.US)
//    df.timeZone = TimeZone.getTimeZone(ApiConstants.JSON_TIMEZONE)
//    df
//  }

    fun toIntegerArray(jsonArray: JsonArray?): Array<Int?>? {
        if (jsonArray == null) return null
        val array = arrayOfNulls<Int>(jsonArray.size())
        for (i in 0 until jsonArray.size()) {
            array[i] = jsonArray.getInteger(i)
        }
        return array
    }

    fun toUUIDArray(jsonArray: JsonArray?): Array<UUID?>? {
        if (jsonArray == null) return null
        val array = arrayOfNulls<UUID>(jsonArray.size())
        for (i in 0 until jsonArray.size()) {
            array[i] = UUID.fromString(jsonArray.getString(i))
        }
        return array
    }

    fun toJsonArray(array: Array<UUID>?): JsonArray? {
        if (array == null) return null
        val jsonArray = JsonArray()
        for (i in array.indices) {
            jsonArray.add(array[i])
        }
        return jsonArray
    }

    fun toJsonArray(array: Array<Int>?): JsonArray? {
        if (array == null) return null
        val jsonArray = JsonArray()
        for (i in array.indices) {
            jsonArray.add(array[i])
        }
        return jsonArray
    }
}
