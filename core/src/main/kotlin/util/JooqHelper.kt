package io.en4ble.pgaccess.util

import io.en4ble.pgaccess.dto.BoxDTO
import io.en4ble.pgaccess.dto.CircleDTO
import io.en4ble.pgaccess.dto.IntervalDTO
import io.en4ble.pgaccess.dto.LineDTO
import io.en4ble.pgaccess.dto.LineSegmentDTO
import io.en4ble.pgaccess.dto.PathDTO
import io.en4ble.pgaccess.dto.PointDTO
import io.en4ble.pgaccess.dto.PolygonDTO
import io.en4ble.pgaccess.enumerations.TypedEnum
import io.vertx.core.json.JsonArray
import io.vertx.kotlin.pgclient.data.boxOf
import io.vertx.kotlin.pgclient.data.circleOf
import io.vertx.kotlin.pgclient.data.intervalOf
import io.vertx.kotlin.pgclient.data.lineOf
import io.vertx.kotlin.pgclient.data.lineSegmentOf
import io.vertx.kotlin.pgclient.data.pathOf
import io.vertx.kotlin.pgclient.data.polygonOf
import io.vertx.pgclient.data.Box
import io.vertx.pgclient.data.Circle
import io.vertx.pgclient.data.Interval
import io.vertx.pgclient.data.Line
import io.vertx.pgclient.data.LineSegment
import io.vertx.pgclient.data.Path
import io.vertx.pgclient.data.Point
import io.vertx.pgclient.data.Polygon
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple
import org.jooq.Condition
import org.jooq.Converter
import org.jooq.Field
import org.jooq.Param
import org.jooq.Query
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object JooqHelper {
    private val LOG by lazy { LoggerFactory.getLogger(JooqHelper::class.java) }

    fun dateTime(date: LocalDate?): LocalDateTime? {
        return if (date == null) null else LocalDateTime.from(date)
    }

    fun logUpdated(updatedRows: Int) {
        LOG.debug("updated {} rows.", updatedRows)
    }

    fun logQuery(query: Query) {
        LOG.debug("running query:\n{}\nvalues:{}", query.sql, query.bindValues)
    }

    fun logSqlError(query: Query, e: Throwable) {
        LOG.error(
            "error running query: " + query.sql + " with values: " + query.bindValues, e
        )
    }

    private fun getDbValues(query: Query): MutableList<Any?> {
        val dbValues = mutableListOf<Any?>()
        query.params.forEach {
            dbValues.add(convertToDbType(it.value))
        }
        return dbValues
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertToDbType(param: Param<*>?): Any? {
        if (param == null) {
            return null
        }
        return convertToDbType(param, param.value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertToDbType(param: Param<*>, value: Any?): Any? {
        if (value == null) {
            return null
        }
        return when (value) {
            is PointDTO -> getPoint(value)
            is LineDTO -> lineOf(value.a, value.b, value.c)
            is LineSegmentDTO -> lineSegmentOf(getPoint(value.a), getPoint(value.b))
            is PolygonDTO -> polygonOf(value.points?.map { getPoint(it) })
            is PathDTO -> pathOf(value.isOpen, value.points?.map { getPoint(it) })
            is CircleDTO -> circleOf(getPoint(value.centre), value.radius)
            is BoxDTO -> boxOf(getPoint(value.sw), getPoint(value.ne))
            is IntervalDTO -> intervalOf(
                value.days,
                value.hours,
                value.microseconds,
                value.minutes,
                value.months,
                value.seconds,
                value.years
            )
            is TypedEnum<*> -> value.key
            is Array<*> -> {
                when {
                    value.isArrayOf<PointDTO>() -> convertArray2DbTypes(value, param, Point::class.java)
                    value.isArrayOf<LineDTO>() -> convertArray2DbTypes(value, param, Line::class.java)
                    value.isArrayOf<LineSegmentDTO>() -> convertArray2DbTypes(value, param, LineSegment::class.java)
                    value.isArrayOf<PolygonDTO>() -> convertArray2DbTypes(value, param, Polygon::class.java)
                    value.isArrayOf<PathDTO>() -> convertArray2DbTypes(value, param, Path::class.java)
                    value.isArrayOf<CircleDTO>() -> convertArray2DbTypes(value, param, Circle::class.java)
                    value.isArrayOf<BoxDTO>() -> convertArray2DbTypes(value, param, Box::class.java)
                    value.isArrayOf<IntervalDTO>() -> convertArray2DbTypes(value, param, Interval::class.java)
                    else -> (param.converter as Converter<Any, Any>).to(value)
                }
            }
            else -> {
                (param.converter as Converter<Any, Any>).to(value)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private inline fun <reified T> convertArray2DbTypes(dtos: Array<*>, param: Param<*>, type: Class<T>): Array<T?> {
        val array = arrayOfNulls<T>(dtos.size)
        for (i in dtos.indices) {
            array[i] = convertToDbType(param, dtos[i]) as T
        }
        return array
    }

    fun convertFromDbType(value: Any?): Any? {
        if (value == null) {
            return null
        }
        return when (value) {
            is Point -> getPointDTO(value)
            else -> null
        }
    }

    private fun getPoint(pointDTO: PointDTO?) = Point(pointDTO?.x?.toDouble() ?: 0.0, pointDTO?.y?.toDouble() ?: 0.0)

    fun params(query: Query): Tuple {
        val values = query.bindValues
        if (values.isEmpty()) {
            throw RuntimeException("query does not contain parameters")
        }
        val dbValues = getDbValues(query)
        return if (dbValues.size == 1) {
            Tuple.of(dbValues[0])
        } else {
            return Tuple.of(dbValues[0], *dbValues.subList(1, dbValues.size).toTypedArray())
        }
    }

    fun rxParams(query: Query): io.vertx.reactivex.sqlclient.Tuple {
        return io.vertx.reactivex.sqlclient.Tuple.newInstance(params(query))
    }

    fun toStringList(res: RowSet<Row>): List<String> {
        return res.toList().map { it.getString(0) }
    }

    fun toUUIDList(res: RowSet<Row>): List<UUID> {
        return res.toList().map { it.getUUID(0) }
    }

    fun toIntegerList(res: RowSet<Row>): List<Int> {
        return res.toList().map { it.getInteger(0) }
    }

    fun getPointDTO(point: Point?): PointDTO? {
        if (point == null) return null
        return PointDTO(point.x.toString(), point.y.toString())
    }

    fun getPointDTOs(points: Array<Point>?): Array<PointDTO>? {
        return points?.map { getPointDTO(it)!! }?.toTypedArray()
    }

    fun getLineDTO(line: Line?): LineDTO? {
        if (line == null) return null
        return LineDTO(line.a, line.b, line.c)
    }

    fun getLineDTOs(lines: Array<Line>?): Array<LineDTO>? {
        return lines?.map { getLineDTO(it)!! }?.toTypedArray()
    }

    fun getLineSegmentDTO(lineSegment: LineSegment?): LineSegmentDTO? {
        if (lineSegment == null) return null
        return LineSegmentDTO(getPointDTO(lineSegment.p1), getPointDTO(lineSegment.p2))
    }

    fun getLineSegmentDTOs(lineSegments: Array<LineSegment>?): Array<LineSegmentDTO>? {
        return lineSegments?.map { getLineSegmentDTO(it)!! }?.toTypedArray()
    }

    fun getPathDTO(path: Path?): PathDTO? {
        if (path == null) return null
        return PathDTO(path.isOpen, getPointDTOs(path.points.toTypedArray())?.toList())
    }

    fun getPathDTOs(paths: Array<Path>?): Array<PathDTO>? {
        return paths?.map { getPathDTO(it)!! }?.toTypedArray()
    }

    fun getPolygonDTO(polygon: Polygon?): PolygonDTO? {
        if (polygon == null) return null
        return PolygonDTO(getPointDTOs(polygon.points.toTypedArray())?.toList())
    }

    fun getPolygonDTOs(paths: Array<Polygon>?): Array<PolygonDTO>? {
        return paths?.map { getPolygonDTO(it)!! }?.toTypedArray()
    }

    fun getBoxDTO(box: Box?): BoxDTO? {
        if (box == null) return null
        return BoxDTO(getPointDTO(box.lowerLeftCorner), getPointDTO(box.upperRightCorner))
    }

    fun getBoxDTOs(boxes: Array<Box>?): Array<BoxDTO>? {
        return boxes?.map { getBoxDTO(it)!! }?.toTypedArray()
    }

    fun getCircleDTO(circle: Circle?): CircleDTO? {
        if (circle == null) return null
        return CircleDTO(getPointDTO(circle.centerPoint), circle.radius)
    }

    fun getCircleDTOs(circles: Array<Circle>?): Array<CircleDTO>? {
        return circles?.map { getCircleDTO(it)!! }?.toTypedArray()
    }

    fun getIntervalDTO(interval: Interval?): IntervalDTO? {
        if (interval == null) return null
        return IntervalDTO(
            interval.years,
            interval.months,
            interval.days,
            interval.hours,
            interval.minutes,
            interval.seconds,
            interval.microseconds
        )
    }

    fun getIntervalDTOs(intervals: Array<Interval>?): Array<IntervalDTO>? {
        return intervals?.map { getIntervalDTO(it)!! }?.toTypedArray()
    }

    fun date(timestamp: LocalDateTime?): LocalDate? {
        return timestamp?.toLocalDate()
    }

    fun uuid(res: JsonArray, pos: Int = 0): UUID {
        return UUID.fromString(res.getString(pos))
    }

    fun any(id: Int, field: Field<*>): Condition {
        return DSL.condition("{0} = ANY({1})", id, field)
    }

    /**
     * Creates a text search vector used for inserting text into a tsvector field.
     */
    fun tsVector(language: String, text: String): Field<Any> {
        return DSL.field("to_tsvector({0},{1})", language, text)
    }

    /**
     * Creates a text search query used for querying text from a tsvector field.
     */
    fun tsQuery(language: String, searchTerm: String): Condition {
        return DSL.condition("j_title_ts @@ to_tsquery(?,?)", language, searchTerm)
    }

    // these function currently rely on the location beeing stored as geography types
    // reactive-pg-client supports only the standard point etc. types
    // FIXME: update queries

    fun stWithin(left: Field<*>, right: Field<*>): Condition {
        return DSL.condition("public.ST_WITHIN({0}, {1})", left, right)
    }

    fun stWithin(left: Field<*>, x: Double, y: Double, distance: Int): Condition {
        return DSL.condition(
            "public.ST_WITHIN({0}, public.geography(public.ST_MakePoint({1},{2})), {3}) = TRUE", left, x, y, distance
        )
    }

    fun stDWithin(left: Field<*>, location: PointDTO, distance: Int): Condition {
        return DSL.condition(
            "public.ST_DWITHIN({0}, public.geography(public.ST_MakePoint({1},{2})), {3}) = TRUE",
            left,
            location.x,
            location.y,
            distance
        )
    }

    fun stIntersects(left: Field<*>, minX: Double, minY: Double, maxX: Double, maxY: Double): Condition {
        return DSL.condition(
            "public.st_intersects({0}, ST_MakeEnvelope({1}, {2}, {3}, {4})) = true",
            left, minX, minY, maxX, maxY
        )
    }

    fun stDWithin(left: Field<*>, x: Double, y: Double, distance: Int): Condition {
        return DSL.condition(
            "public.ST_DWITHIN({0}, public.geography(public.ST_MakePoint({1},{2})), {3}) = TRUE", left, x, y, distance
        )
    }

    fun stDistance(location: Field<*>, x: Double, y: Double): Condition {
        return DSL.condition(
            "public.st_distance(" + location.name + ", public.geography(public.ST_MakePoint({0},{1}))", x, y
        )
    }

    fun stDistance(x: Double, y: Double): Condition {
        return DSL.condition("public.st_distance(mp_location, public.geography(public.ST_MakePoint({0},{1}))", x, y)
    }
}
