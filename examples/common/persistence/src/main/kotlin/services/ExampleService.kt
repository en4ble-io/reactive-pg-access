package io.en4ble.examples.services

import io.en4ble.examples.dao.ExampleDao
import io.en4ble.examples.dto.TestDTO
import io.en4ble.examples.jooq.tables.Example.EXAMPLE
import io.en4ble.examples.jooq.tables.pojos.ExampleDto
import io.en4ble.pgaccess.dto.*
import java.time.*
import java.util.*
import kotlin.random.Random

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
open class ExampleService(protected val exampleDao: ExampleDao) {
    suspend fun create(): ExampleDto {
        val form = ExampleDto()
        form.uuid = UUID.randomUUID()

        form.booleanValue = true
        form.setBooleanArray(false, true)

//        form.box = randomBox()
//        form.setBoxArray(randomBox(), randomBox())

//        form.circle = randomCircle()
//        form.setCircleArray(randomCircle(), randomCircle())

        form.date = LocalDate.now()
        form.setDateArray(LocalDate.now(), LocalDate.now())

        form.dateTime = LocalDateTime.now()
        form.setDateTimeArray(LocalDateTime.now(), LocalDateTime.now())

        form.dateTimetz = OffsetDateTime.now()
        form.setDateTimetzArray(OffsetDateTime.now(), OffsetDateTime.now())

        form.doubleValue = Random.nextDouble(10.0)
        form.setDoubleArray(Random.nextDouble(10.0), Random.nextDouble(10.0))

        form.floatValue = Random.nextFloat()
        form.setFloatArray(Random.nextFloat(), Random.nextFloat())

        form.integerValue = Random.nextInt(100)
        form.setIntegerArray(Random.nextInt(100), Random.nextInt(100), Random.nextInt(100))

// FIXME: support interval
//        form.interval = IntervalDTO(2)
//        form.setIntervalArray(IntervalDTO(2), IntervalDTO(0, 2))

// FIXME: fix json data types
//        form.jsonb = randomTestDTO()
//        form.setJsonbArray(randomTestDTO(), randomTestDTO())

//        form.line = randomLine()
//        form.setLineArray(randomLine(), randomLine())

//        form.lineSegment = randomLineSegment()
//        form.setLineSegmentArray(
//            randomLineSegment(), randomLineSegment()
//        )

        form.longValue = Random.nextLong()
        form.setLongArray(Random.nextLong(), Random.nextLong())

        form.longSerial = Random.nextLong()

        form.name = randomString()
        form.setNameArray(randomString(), randomString())

        form.path = randomPath()
        form.setPathArray(randomPath(), randomPath())

        form.point = randomPoint()
        form.setPointArray(randomPoint(), randomPoint())

//        form.polygon = randomPolygon()
//        form.setPolygonArray(randomPolygon(), randomPolygon())

        form.string = randomString()
        form.setStringArray(randomString(), randomString())

        form.shortValue = randomShort()
        form.setShortArray(randomShort(), randomShort())

        form.shortSerial = randomShort()

        form.serial = Random.nextInt(1000)

        form.text = randomString(200)
        form.setTextArray(randomString(200), randomString(200))

        form.time = LocalTime.now()
        form.setTimeArray(LocalTime.now(), LocalTime.NOON)

        form.timetz = OffsetTime.now()
        form.setTimetzArray(OffsetTime.now(), OffsetTime.now())

        form.setUuidArray(UUID.randomUUID(), UUID.randomUUID())

        return exampleDao.createReturning(form)
    }

    private fun randomShort() = Random.nextInt(10).toShort()

    suspend fun update(): ExampleDto {
        val example = exampleDao.readRandom()
        return exampleDao.updateReturning(
            Pair(EXAMPLE.BOOLEAN_VALUE, Random.nextBoolean()),
            Pair(EXAMPLE.UUID_ARRAY, arrayOf(UUID.randomUUID(), UUID.randomUUID())),
            Pair(EXAMPLE.STRING, randomString()),
            Pair(EXAMPLE.STRING_ARRAY, arrayOf(randomString(), randomString())),
            Pair(EXAMPLE.TEXT, randomString()),
            Pair(EXAMPLE.TEXT_ARRAY, arrayOf(randomString(300), randomString(300))),
            Pair(EXAMPLE.DATE, LocalDate.now()),
            Pair(EXAMPLE.CIRCLE_ARRAY, arrayOf(randomCircle(), randomCircle())),
            Pair(EXAMPLE.DOUBLE_VALUE, Random.nextDouble(1000.0)),
            Pair(EXAMPLE.POINT, randomPoint()),
            EXAMPLE.UUID.eq(example.uuid)
        ).first()
    }

    suspend fun update2(): ExampleDto {
        val example = exampleDao.readRandom()
        val form = ExampleDto()
        form.string = randomString()
        form.setTextArray(randomString(200), randomString(200))
        return exampleDao.updateReturning(form, EXAMPLE.UUID.eq(example.uuid)).first()
    }

    private fun randomString(length: Int = 20): String {
        val charPool = mutableListOf<Char>()
        for (c: Char in 'a'..'z') {
            charPool.add(c)
        }
        for (c: Char in 'A'..'Z') {
            charPool.add(c)
        }
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun randomPath() = PathDTO(true, listOf(randomPoint(), randomPoint()))

    private fun randomPolygon() = PolygonDTO(listOf(randomPoint(), randomPoint(), randomPoint(), randomPoint()))

    private fun randomLineSegment() = LineSegmentDTO(randomPoint(), randomPoint())

    private fun randomLine() = LineDTO(
        Random.nextDouble(9.0),
        Random.nextDouble(9.0, 200.0),
        Random.nextDouble(200.0, 300.0)
    )

    private fun randomTestDTO(): TestDTO {
        val i = Random.nextInt(1000)
        return TestDTO("test name $i", "test description $i")
    }

    private fun randomBox() = BoxDTO(randomPoint(), randomPoint())

    private fun randomCircle() = CircleDTO(randomPoint(), Random.nextDouble(0.9, 10.0))

    private fun randomPoint() =
        PointDTO(Random.nextDouble(0.1, 90.0), Random.nextDouble(0.1, 90.0))
}
