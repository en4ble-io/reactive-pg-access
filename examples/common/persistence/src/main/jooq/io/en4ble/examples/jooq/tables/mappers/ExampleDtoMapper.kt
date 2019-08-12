/**
 * This class is generated by jOOQ.
 */
package io.en4ble.examples.jooq.tables.mappers
import io.en4ble.examples.jooq.tables.pojos.ExampleDto
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "RemoveRedundantQualifierName")
class ExampleDtoMapper:io.en4ble.pgaccess.mappers.AbstractJooqMapper<ExampleDto>() {
    companion object {
        private val instance = io.en4ble.examples.jooq.tables.mappers.ExampleDtoMapper()
        fun instance():ExampleDtoMapper {
            return instance
        }
        fun map(row:io.vertx.sqlclient.Row):ExampleDto {
            return instance.toDto(row)
        }
        fun map(res:io.vertx.sqlclient.RowSet ):List<ExampleDto>  {
            return instance.toList(res)
        }
    }
    @SuppressWarnings("Duplicates", "unused")
override fun toDto(row:io.vertx.sqlclient.Row, offset:Int):ExampleDto {
        val dto = ExampleDto()
        dto.setUuid(row.getUUID(offset))
        val uuidArray = row.getUUIDArray(offset+1)
        if(uuidArray != null) {
            dto.setUuidArray(*uuidArray)
        }
        dto.setText(row.getString(offset+2))
        val textArray = row.getStringArray(offset+3)
        if(textArray != null) {
            dto.setTextArray(*textArray)
        }
        dto.setString(row.getString(offset+4))
        val stringArray = row.getStringArray(offset+5)
        if(stringArray != null) {
            dto.setStringArray(*stringArray)
        }
        dto.setShortValue(row.getShort(offset+6))
        val shortArray = row.getShortArray(offset+7)
        if(shortArray != null) {
            dto.setShortArray(*shortArray)
        }
        dto.setIntegerValue(row.getInteger(offset+8))
        val integerArray = row.getIntegerArray(offset+9)
        if(integerArray != null) {
            dto.setIntegerArray(*integerArray)
        }
        dto.setLongValue(row.getLong(offset+10))
        val longArray = row.getLongArray(offset+11)
        if(longArray != null) {
            dto.setLongArray(*longArray)
        }
        dto.setFloatValue(row.getFloat(offset+12))
        val floatArray = row.getFloatArray(offset+13)
        if(floatArray != null) {
            dto.setFloatArray(*floatArray)
        }
        dto.setDoubleValue(row.getDouble(offset+14))
        val doubleArray = row.getDoubleArray(offset+15)
        if(doubleArray != null) {
            dto.setDoubleArray(*doubleArray)
        }
        dto.setBooleanValue(row.getBoolean(offset+16))
        val booleanArray = row.getBooleanArray(offset+17)
        if(booleanArray != null) {
            dto.setBooleanArray(*booleanArray)
        }
        dto.setDate(row.getLocalDate(offset+18))
        val dateArray = row.getLocalDateArray(offset+19)
        if(dateArray != null) {
            dto.setDateArray(*dateArray)
        }
        dto.setTime(row.getLocalTime(offset+20))
        val timeArray = row.getLocalTimeArray(offset+21)
        if(timeArray != null) {
            dto.setTimeArray(*timeArray)
        }
        dto.setTimetz(row.getOffsetTime(offset+22))
        val timetzArray = row.getOffsetTimeArray(offset+23)
        if(timetzArray != null) {
            dto.setTimetzArray(*timetzArray)
        }
        dto.setDateTime(row.getLocalDateTime(offset+24))
        val dateTimeArray = row.getLocalDateTimeArray(offset+25)
        if(dateTimeArray != null) {
            dto.setDateTimeArray(*dateTimeArray)
        }
        dto.setDateTimetz(row.getOffsetDateTime(offset+26))
        val dateTimetzArray = row.getOffsetDateTimeArray(offset+27)
        if(dateTimetzArray != null) {
            dto.setDateTimetzArray(*dateTimetzArray)
        }
        val e_jsonb = row.get(io.vertx.core.json.JsonObject::class.java,offset+28)
        if (e_jsonb != null) {
            val e_jsonb_converted=(io.en4ble.examples.jooq.tables.Example.EXAMPLE.JSONB.converter as io.en4ble.examples.converters.TestDTOConverter).from(e_jsonb)
            if(e_jsonb_converted != null) {
                dto.setJsonb(e_jsonb_converted)
            }
        }
        dto.setPoint(io.en4ble.pgaccess.util.JooqHelper.getPointDTO(row.get(io.vertx.pgclient.data.Point::class.java,offset+29)))
        val pointArray = io.en4ble.pgaccess.util.JooqHelper.getPointDTOs(row.getValues(io.vertx.pgclient.data.Point::class.java,offset+30))
        if(pointArray != null) {
            dto.setPointArray(*pointArray)
        }
        dto.setLine(io.en4ble.pgaccess.util.JooqHelper.getLineDTO(row.get(io.vertx.pgclient.data.Line::class.java,offset+31)))
        val lineArray = io.en4ble.pgaccess.util.JooqHelper.getLineDTOs(row.getValues(io.vertx.pgclient.data.Line::class.java,offset+32))
        if(lineArray != null) {
            dto.setLineArray(*lineArray)
        }
        dto.setLineSegment(io.en4ble.pgaccess.util.JooqHelper.getLineSegmentDTO(row.get(io.vertx.pgclient.data.LineSegment::class.java,offset+33)))
        val lineSegmentArray = io.en4ble.pgaccess.util.JooqHelper.getLineSegmentDTOs(row.getValues(io.vertx.pgclient.data.LineSegment::class.java,offset+34))
        if(lineSegmentArray != null) {
            dto.setLineSegmentArray(*lineSegmentArray)
        }
        dto.setBox(io.en4ble.pgaccess.util.JooqHelper.getBoxDTO(row.get(io.vertx.pgclient.data.Box::class.java,offset+35)))
        val boxArray = io.en4ble.pgaccess.util.JooqHelper.getBoxDTOs(row.getValues(io.vertx.pgclient.data.Box::class.java,offset+36))
        if(boxArray != null) {
            dto.setBoxArray(*boxArray)
        }
        dto.setPath(io.en4ble.pgaccess.util.JooqHelper.getPathDTO(row.get(io.vertx.pgclient.data.Path::class.java,offset+37)))
        val pathArray = io.en4ble.pgaccess.util.JooqHelper.getPathDTOs(row.getValues(io.vertx.pgclient.data.Path::class.java,offset+38))
        if(pathArray != null) {
            dto.setPathArray(*pathArray)
        }
        dto.setPolygon(io.en4ble.pgaccess.util.JooqHelper.getPolygonDTO(row.get(io.vertx.pgclient.data.Polygon::class.java,offset+39)))
        val polygonArray = io.en4ble.pgaccess.util.JooqHelper.getPolygonDTOs(row.getValues(io.vertx.pgclient.data.Polygon::class.java,offset+40))
        if(polygonArray != null) {
            dto.setPolygonArray(*polygonArray)
        }
        dto.setCircle(io.en4ble.pgaccess.util.JooqHelper.getCircleDTO(row.get(io.vertx.pgclient.data.Circle::class.java,offset+41)))
        val circleArray = io.en4ble.pgaccess.util.JooqHelper.getCircleDTOs(row.getValues(io.vertx.pgclient.data.Circle::class.java,offset+42))
        if(circleArray != null) {
            dto.setCircleArray(*circleArray)
        }
        dto.setName(row.getString(offset+43))
        val nameArray = row.getStringArray(offset+44)
        if(nameArray != null) {
            dto.setNameArray(*nameArray)
        }
        dto.setInterval(io.en4ble.pgaccess.util.JooqHelper.getIntervalDTO(row.get(io.vertx.pgclient.data.Interval::class.java,offset+45)))
        val intervalArray = io.en4ble.pgaccess.util.JooqHelper.getIntervalDTOs(row.getValues(io.vertx.pgclient.data.Interval::class.java,offset+46))
        if(intervalArray != null) {
            dto.setIntervalArray(*intervalArray)
        }
        dto.setShortSerial(row.getShort(offset+47))
        dto.setSerial(row.getInteger(offset+48))
        dto.setLongSerial(row.getLong(offset+49))
        dto.setState(row.getString(offset+50))
        return dto
    }
    override fun getValueMap(o:Any):Map<org.jooq.Field<*>,*>  {
        val dto = o as ExampleDto
        val map = mutableMapOf<org.jooq.Field<*>,Any>()
        val uuid=dto.uuid
        if(uuid !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.UUID,uuid)
        }
        val uuidArray=dto.uuidArray
        if(uuidArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.UUID_ARRAY,uuidArray)
        }
        val text=dto.text
        if(text !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TEXT,text)
        }
        val textArray=dto.textArray
        if(textArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TEXT_ARRAY,textArray)
        }
        val string=dto.string
        if(string !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.STRING,string)
        }
        val stringArray=dto.stringArray
        if(stringArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.STRING_ARRAY,stringArray)
        }
        val shortValue=dto.shortValue
        if(shortValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.SHORT_VALUE,shortValue)
        }
        val shortArray=dto.shortArray
        if(shortArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.SHORT_ARRAY,shortArray)
        }
        val integerValue=dto.integerValue
        if(integerValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.INTEGER_VALUE,integerValue)
        }
        val integerArray=dto.integerArray
        if(integerArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.INTEGER_ARRAY,integerArray)
        }
        val longValue=dto.longValue
        if(longValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LONG_VALUE,longValue)
        }
        val longArray=dto.longArray
        if(longArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LONG_ARRAY,longArray)
        }
        val floatValue=dto.floatValue
        if(floatValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.FLOAT_VALUE,floatValue)
        }
        val floatArray=dto.floatArray
        if(floatArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.FLOAT_ARRAY,floatArray)
        }
        val doubleValue=dto.doubleValue
        if(doubleValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DOUBLE_VALUE,doubleValue)
        }
        val doubleArray=dto.doubleArray
        if(doubleArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DOUBLE_ARRAY,doubleArray)
        }
        val booleanValue=dto.booleanValue
        if(booleanValue !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.BOOLEAN_VALUE,booleanValue)
        }
        val booleanArray=dto.booleanArray
        if(booleanArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.BOOLEAN_ARRAY,booleanArray)
        }
        val date=dto.date
        if(date !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE,date)
        }
        val dateArray=dto.dateArray
        if(dateArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE_ARRAY,dateArray)
        }
        val time=dto.time
        if(time !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TIME,time)
        }
        val timeArray=dto.timeArray
        if(timeArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TIME_ARRAY,timeArray)
        }
        val timetz=dto.timetz
        if(timetz !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TIMETZ,timetz)
        }
        val timetzArray=dto.timetzArray
        if(timetzArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.TIMETZ_ARRAY,timetzArray)
        }
        val dateTime=dto.dateTime
        if(dateTime !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE_TIME,dateTime)
        }
        val dateTimeArray=dto.dateTimeArray
        if(dateTimeArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE_TIME_ARRAY,dateTimeArray)
        }
        val dateTimetz=dto.dateTimetz
        if(dateTimetz !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE_TIMETZ,dateTimetz)
        }
        val dateTimetzArray=dto.dateTimetzArray
        if(dateTimetzArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.DATE_TIMETZ_ARRAY,dateTimetzArray)
        }
        val jsonb=dto.jsonb
        if(jsonb !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.JSONB,jsonb)
        }
        val point=dto.point
        if(point !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.POINT,point)
        }
        val pointArray=dto.pointArray
        if(pointArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.POINT_ARRAY,pointArray)
        }
        val line=dto.line
        if(line !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LINE,line)
        }
        val lineArray=dto.lineArray
        if(lineArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LINE_ARRAY,lineArray)
        }
        val lineSegment=dto.lineSegment
        if(lineSegment !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LINE_SEGMENT,lineSegment)
        }
        val lineSegmentArray=dto.lineSegmentArray
        if(lineSegmentArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LINE_SEGMENT_ARRAY,lineSegmentArray)
        }
        val box=dto.box
        if(box !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.BOX,box)
        }
        val boxArray=dto.boxArray
        if(boxArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.BOX_ARRAY,boxArray)
        }
        val path=dto.path
        if(path !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.PATH,path)
        }
        val pathArray=dto.pathArray
        if(pathArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.PATH_ARRAY,pathArray)
        }
        val polygon=dto.polygon
        if(polygon !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.POLYGON,polygon)
        }
        val polygonArray=dto.polygonArray
        if(polygonArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.POLYGON_ARRAY,polygonArray)
        }
        val circle=dto.circle
        if(circle !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.CIRCLE,circle)
        }
        val circleArray=dto.circleArray
        if(circleArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.CIRCLE_ARRAY,circleArray)
        }
        val name=dto.name
        if(name !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.NAME,name)
        }
        val nameArray=dto.nameArray
        if(nameArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.NAME_ARRAY,nameArray)
        }
        val interval=dto.interval
        if(interval !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.INTERVAL,interval)
        }
        val intervalArray=dto.intervalArray
        if(intervalArray !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.INTERVAL_ARRAY,intervalArray)
        }
        val shortSerial=dto.shortSerial
        if(shortSerial !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.SHORT_SERIAL,shortSerial)
        }
        val serial=dto.serial
        if(serial !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.SERIAL,serial)
        }
        val longSerial=dto.longSerial
        if(longSerial !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.LONG_SERIAL,longSerial)
        }
        val state=dto.state
        if(state !=null) {
            map.put(io.en4ble.examples.jooq.tables.Example.EXAMPLE.STATE,state)
        }
        return map
    }
}
