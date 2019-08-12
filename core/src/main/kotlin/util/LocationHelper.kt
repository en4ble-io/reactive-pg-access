package io.en4ble.pgaccess.util

import io.vertx.pgclient.data.Point
import org.slf4j.LoggerFactory

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object LocationHelper {
    private val LOG by lazy { LoggerFactory.getLogger(LocationHelper::class.java) }
    private const val NULL = "null"

    fun getApproximatedLocation(point: Point?): Point? {
        return if (point != null) {
            Point(
                getApproximatedValue(point.x),
                getApproximatedValue(point.y)
            )
        } else null
    }

    private fun getApproximatedValue(value: Double): Double {
        val s = value.toString()
        val parts = s.split('.')
        val i = parts[0]
        var decimals = parts[1]
        // lat + lng have usually 6 decimals, cut off the last 2
        if (decimals.length > 4) {
            decimals = decimals.substring(0, 4)
        }
        // now add 2 random decimals
        //  + RandomUtils.nextInt(0, 10) + RandomUtils.nextInt(0, 10)
        return "$i.$decimals".toDouble()
    }
}
