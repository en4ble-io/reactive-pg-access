package io.en4ble.examples.jooq.tables.mappers
object PgaccessMappers {
    private val mappers = mutableMapOf<org.jooq.Table<*>, io.en4ble.pgaccess.mappers.JooqMapper<*>>()
    init {
        mappers[io.en4ble.examples.jooq.tables.ConferenceV1.CONFERENCE_V1] = io.en4ble.examples.jooq.tables.mappers.ConferenceV1DtoMapper.instance()
        mappers[io.en4ble.examples.jooq.tables.ConferenceV1.CONFERENCE_V1] = io.en4ble.examples.jooq.tables.mappers.ConferenceV1FormMapper.instance()
        mappers[io.en4ble.examples.jooq.tables.Conference.CONFERENCE] = io.en4ble.examples.jooq.tables.mappers.ConferenceDtoMapper.instance()
        mappers[io.en4ble.examples.jooq.tables.Conference.CONFERENCE] = io.en4ble.examples.jooq.tables.mappers.ConferenceFormMapper.instance()
        mappers[io.en4ble.examples.jooq.tables.Example.EXAMPLE] = io.en4ble.examples.jooq.tables.mappers.ExampleDtoMapper.instance()
        mappers[io.en4ble.examples.jooq.tables.Example.EXAMPLE] = io.en4ble.examples.jooq.tables.mappers.ExampleFormMapper.instance()
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> getMapper(table:org.jooq.Table<*>):io.en4ble.pgaccess.mappers.JooqMapper<T> {
        val mapper = mappers[table] ?: throw RuntimeException("No mapper for table $table found!")
        return mapper as io.en4ble.pgaccess.mappers.JooqMapper<T>
    }
}
