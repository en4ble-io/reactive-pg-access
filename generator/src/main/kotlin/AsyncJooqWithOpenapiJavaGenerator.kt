package io.en4ble.pgaccess.generator

import io.en4ble.pgaccess.dto.BoxDTO
import io.en4ble.pgaccess.dto.CircleDTO
import io.en4ble.pgaccess.dto.IntervalDTO
import io.en4ble.pgaccess.dto.LineDTO
import io.en4ble.pgaccess.dto.LineSegmentDTO
import io.en4ble.pgaccess.dto.PathDTO
import io.en4ble.pgaccess.dto.PointDTO
import io.en4ble.pgaccess.dto.PolygonDTO
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.data.Box
import io.vertx.pgclient.data.Circle
import io.vertx.pgclient.data.Interval
import io.vertx.pgclient.data.Line
import io.vertx.pgclient.data.LineSegment
import io.vertx.pgclient.data.Path
import io.vertx.pgclient.data.Point
import io.vertx.pgclient.data.Polygon
import io.vertx.sqlclient.data.Numeric
import org.jooq.Constants
import org.jooq.Record
import org.jooq.codegen.ExtendedJavaGenerator
import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.GeneratorStrategy.Mode
import org.jooq.codegen.JavaWriter
import org.jooq.meta.CatalogDefinition
import org.jooq.meta.ColumnDefinition
import org.jooq.meta.DataTypeDefinition
import org.jooq.meta.Definition
import org.jooq.meta.SchemaDefinition
import org.jooq.meta.TableDefinition
import org.jooq.meta.TypedElementDefinition
import org.jooq.meta.UDTDefinition
import org.jooq.tools.JooqLogger
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Collections.emptyList
import java.util.UUID
import kotlin.math.absoluteValue

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
open class AsyncJooqWithOpenapiJavaGenerator : ExtendedJavaGenerator() {
    private val LOG = JooqLogger.getLogger(AsyncJooqWithOpenapiJavaGenerator::class.java)
    private lateinit var en4bleGeneratorStrategy: TablePrefixAwareGeneratorStrategy
    protected open val readonlyDaoBaseClassFqn = "io.en4ble.pgaccess.AsyncDaoBase"
    protected open val updatableDaoBaseClassFqn = "io.en4ble.pgaccess.UpdatableAsyncDaoBase"

    init {
        println("Customized generator for JOOQ with support for OpenApi and JAXRS")
        println("You can use the following in your column definitions to control the code generation:")
        println("table comment contains {{view}} - The table is regarded as a view - no write/update methods will be generated.")
        println("(note: if you want to use readPage / seek queries only, you need to override the primaryKeyField() method in your DAO to return the ID column of the view)")
//        println("comment contains {{viewId=<string>}} - The field to be used as primary key in views - optional, required for readPage / seek queries only.")
        println("comment contains {{name=<string>}} - sets the json name of the property.")
        println("comment contains {{example=<string>}} - sets the example value of the property in the OpenApi @Schema annotation.")
        println("comment contains {{minLength=<int>}} - generates @org.hibernate.validator.constraints.Length(min=<int>)")
        println("comment contains {{maxLength=<int>}} - generates @org.hibernate.validator.constraints.Length(max=<int>) ")
        println("comment contains {{email}} or column name contains 'email' - generates @javax.validation.constraints.Email")
        println("comment contains {{default=<string>}} - generates defaultValue=\"your value\" - Use this with TypedEnum columns to override the database default value.")
        println("comment contains {{readOnly}} - generates accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY")
        println("comment contains {{writeOnly}} - generates accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY")
        println("comment contains {{generated}} - indicates that a field is generated (e.g. id, creation date, update date) and is therefore readOnly as well as not checked for null via the API (DB validation remains of course).")
        println("comment contains {{internal}} - generates @io.swagger.v3.oas.annotations.Hidden + @com.fasterxml.jackson.annotation.JsonIgnore - Use this to hide fields from the public API definitions.")
        println("column name starts with 'internal_' - same as {{internal}} but can be used e.g. in database views to clearly indicate which fields will not be included in the public API.")
    }

    override fun printTableJPAAnnotation(out: JavaWriter, table: TableDefinition) {
        super.printTableJPAAnnotation(out, table)
        val name = getStrategy().getJavaClassName(table, Mode.DEFAULT)
        printSchemaAnnotation(out, name, table.comment, null, null, null, 0)
    }

    override fun printColumnJPAAnnotation(out: JavaWriter, column: ColumnDefinition) {
        val fullComment = column.comment
        val userType = column.type.userType
        var comment: String? = null
        var isInternal = false
        val propertyName = getStrategy().getJavaMemberName(column)
        val columnName = column.name
        var minLength = 0
        var maxLength = getMaxLength(column)
        var defaultValue: String? = null
        var accessMode = "READ_WRITE"
        var generated = false
        var nameValue: String? = null
        var exampleValue: String? = null

        if (columnName.toLowerCase().contains("email")) {
            out.tab(1).println("@javax.validation.constraints.Email")
        }
        if (fullComment != null && fullComment.isNotEmpty()) {
            if (fullComment.contains("{{internal}}")) {
                comment = fullComment.replace("{{internal}}", "")
                isInternal = true
            } else {
                comment = fullComment
            }
            if (comment.contains("{{generated}}")) {
                generated = true
                accessMode = "READ_ONLY"
                comment = comment.replace("{{generated}}", "")
            }
            if (comment.contains("{{email}}")) {
                out.tab(1).println("@javax.validation.constraints.Email")
                comment = comment.replace("{{email}}", "")
            }

            val primaryKey = column.primaryKey
            if ((primaryKey != null && primaryKey.isPrimaryKey) // mark primary key fields as read only
                || comment.contains("{{readOnly}}")
            ) {
                accessMode = "READ_ONLY"
                comment = comment.replace("{{readOnly}}", "")
            }
            if (comment.contains("{{writeOnly}}")) {
                accessMode = "WRITE_ONLY"
                comment = comment.replace("{{writeOnly}}", "")
            }

            val maxLengthPair = parseCommentWithValue("maxLength", comment)
            comment = maxLengthPair.first
            maxLength = maxLengthPair.second?.toInt() ?: getMaxLength(column)

            val minLengthPair = parseCommentWithValue("minLength", comment)
            comment = minLengthPair.first
            minLength = minLengthPair.second?.toInt() ?: 0

            val defaultValuePair = parseCommentWithValue("default", comment)
            comment = defaultValuePair.first
            defaultValue = defaultValuePair.second

            val nameValuePair = parseCommentWithValue("name", comment)
            comment = nameValuePair.first
            nameValue = nameValuePair.second

            val examplePair = parseCommentWithValue("example", comment)
            comment = examplePair.first
            exampleValue = examplePair.second

            comment = comment.trim()
        }
        if (nameValue == null) {
            nameValue = propertyName
        }
        if (defaultValue == null) {
            defaultValue = column.definedType.defaultValue
        }
        if (!comment.isNullOrBlank()) {
            out.tab(1).println("/**")
            printJavadocParagraph(out.tab(1), comment, "")
            out.tab(1).println(" */")
        }
        if (!generated && !column.definedType.isNullable) {
            out.tab(1).println("@javax.validation.constraints.NotNull")
        }
        val isArray = userType.startsWith('_')
        if (!isArray) {
            // TODO: find a way to check for the length of elements of an array and use that annotation on string arrays
            printLengthAnnotation(out, minLength, maxLength)
        }
        super.printColumnJPAAnnotation(out, column)

        if (columnName.startsWith("internal_")) {
            isInternal = true
        }
        // allow internal fields to also be specified using a prefix
        if (isInternal) {
            out.tab(1).println("@io.swagger.v3.oas.annotations.Hidden")
            out.tab(1).println("@com.fasterxml.jackson.annotation.JsonIgnore")
        } else {
            printSchemaAnnotation(
                out,
                propertyName,
                comment,
                defaultValue,
                accessMode,
                exampleValue,
                1
            )
        }
        if (!isInternal) {
            out.tab(1)
                .println("@com.fasterxml.jackson.annotation.JsonProperty(value=\"$nameValue\", access = com.fasterxml.jackson.annotation.JsonProperty.Access.$accessMode)")
        }
    }

    private fun parseCommentWithValue(
        name: String,
        comment: String
    ): Pair<String, String?> {
        val valueStart = comment.indexOf("{{$name=")
        var updatedComment: String = comment
        var value: String? = null
        if (valueStart > -1) {
            val valueEnd = comment.indexOf("}}", valueStart)
            if (valueEnd == -1) {
                throw RuntimeException("could not get end of comment: $comment")
            }
            value = comment.substring(valueStart + name.length + 3, valueEnd)
            updatedComment = comment.removeRange(valueStart, valueEnd + 2)
        }
        return Pair(updatedComment, value)
    }

    private fun getMaxLength(column: ColumnDefinition): Int {
        return column.definedType.length.absoluteValue
    }

    private fun printLengthAnnotation(out: JavaWriter, minLength: Int, maxLength: Int) {
        if (maxLength > 0) {
            if (minLength > 0) {
                out.tab(1).println("@org.hibernate.validator.constraints.Length(min = $minLength, max = $maxLength)")
            } else {
                out.tab(1).println("@org.hibernate.validator.constraints.Length(max = $maxLength)")
            }
        } else if (minLength > 0) {
            out.tab(1).println("@org.hibernate.validator.constraints.Length(min = $minLength)")
        }
    }

    private fun printSchemaAnnotation(
        out: JavaWriter,
        name: String?,
        comment: String?,
        defaultValue: String? = null,
        accessMode: String?,
        example: String?,
        tab: Int
    ) {
        val access =
            if (accessMode == null) {
                ""
            } else {
                "accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.$accessMode, "
            }
        if (comment != null || name != null || example != null) {
            val attrs = mutableListOf<String>()
            if (name != null) attrs.add("name=\"$name\"")
            if (comment != null) attrs.add("title=\"$comment\"")
            if (example != null) attrs.add("example=\"$example\"")
            var attrsString = attrs.joinToString(", ")

            if (!defaultValue.isNullOrBlank()) {
                LOG.debug("$name defaultValue: $defaultValue")
                val s = if (defaultValue.contains('\'')) {
                    defaultValue.substring(1, defaultValue.indexOf('\'', 1))
                } else defaultValue
                if (attrsString.isNotEmpty()) {
                    attrsString += ", "
                }
                out.tab(tab)
                    .println("@io.swagger.v3.oas.annotations.media.Schema($access${attrsString}defaultValue=\"$s\")")
            } else {
                out.tab(tab)
                    .println("@io.swagger.v3.oas.annotations.media.Schema($access$attrsString)")
            }
        }
    }

    override fun generateDaoClassFooter(table: TableDefinition, out: JavaWriter) {
        super.generateDaoClassFooter(table, out)
        val mappers = getMappersClassName(table.schema)
        val mapperPackage = getMapperPackage(table)
        // if the table is marked with a {{view}} comment it is regarded as a view and will be read only.
        val readonly = table.comment != null && table.comment.contains("{{view}}")
        val viewIdName = if (table.comment != null && table.comment.contains("{{viewId}}")) {
            val viewIdNamePair = parseCommentWithValue("viewId", table.comment)
            viewIdNamePair.second
        } else {
            null
        }
        val dtoType = out.ref(getStrategy().getFullJavaClassName(table, Mode.POJO))

        val fullJavaTableName = getFullJavaTableName(table)
        val tableRecord = out.ref(getStrategy().getFullJavaClassName(table, Mode.RECORD))

        if (viewIdName != null) {
            // TODO: override primaryKeyField function
        }

        out.tab(1)
            .println("override fun map(row:io.vertx.sqlclient.Row, table:org.jooq.Table<$tableRecord>,offset:Int): $dtoType {")
        out.tab(2)
            .println("return ${dtoMapper(mapperPackage, mappers, dtoType)}(table).toDto(row, offset)")
        out.tab(1).println("}")

        out.tab(1)
            .println("override fun map(rs:io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row> , table:org.jooq.Table<$tableRecord>, offset:Int): List<$dtoType>  {")
        out.tab(2)
            .println("val mapper = ${dtoMapper(mapperPackage, mappers, dtoType)}(table)")
        out.tab(2).println("return mapper.toList(rs,offset)")
        out.tab(1).println("}")

        out.tab(1).println("override fun map(row:io.vertx.sqlclient.Row, offset:Int):$dtoType {")
        out.tab(2).println("return map(row, $fullJavaTableName,offset)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun map(rs:io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row> , offset:Int):List<$dtoType> {")
        out.tab(2).println("return map(rs, $fullJavaTableName, offset)")
        out.tab(1).println("}")

        out.tab(1).println("override suspend fun read(condition:org.jooq.Condition):List<$dtoType> {")
        out.tab(1).println("return read(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun read(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):List<$dtoType> {")
        out.tab(1).println("return read(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override fun rxRead(condition:org.jooq.Condition):io.reactivex.Single<List<$dtoType>> {")
        out.tab(1).println("return rxRead(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxRead(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<List<$dtoType>> {")
        out.tab(1).println("return rxRead(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override suspend fun read(query:org.jooq.Query):List<$dtoType> {")
        out.tab(1).println("return read(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun read(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):List<$dtoType> {")
        out.tab(1).println("return read(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override fun rxRead(query:org.jooq.Query):io.reactivex.Single<List<$dtoType>> {")
        out.tab(1).println("return rxRead(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxRead(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<List<$dtoType>> {")
        out.tab(1).println("return rxRead(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override suspend fun readOne(condition:org.jooq.Condition):$dtoType {")
        out.tab(1).println("return readOne(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun readOne(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):$dtoType {")
        out.tab(1).println("return readOne(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override fun rxReadOne(condition:org.jooq.Condition):io.reactivex.Single<$dtoType> {")
        out.tab(1).println("return rxReadOne(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxReadOne(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<$dtoType> {")
        out.tab(1).println("return rxReadOne(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override suspend fun readOne(query:org.jooq.Query):$dtoType {")
        out.tab(1).println("return readOne(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun readOne(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):$dtoType {")
        out.tab(1).println("return readOne(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override fun rxReadOne(query:org.jooq.Query):io.reactivex.Single<$dtoType> {")
        out.tab(1).println("return rxReadOne(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxReadOne(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<$dtoType> {")
        out.tab(1).println("return rxReadOne(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1)
            .println("override suspend fun readOptional(condition:org.jooq.Condition):java.util.Optional<$dtoType> {")
        out.tab(1).println("return readOptional(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun readOptional(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):java.util.Optional<$dtoType> {")
        out.tab(1).println("return readOptional(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1)
            .println("override fun rxReadOptional(condition:org.jooq.Condition):io.reactivex.Single<java.util.Optional<$dtoType>> {")
        out.tab(1).println("return rxReadOptional(condition, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxReadOptional(condition:org.jooq.Condition,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<java.util.Optional<$dtoType>> {")
        out.tab(1).println("return rxReadOptional(condition, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1).println("override suspend fun readOptional(query:org.jooq.Query):java.util.Optional<$dtoType> {")
        out.tab(1).println("return readOptional(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override suspend fun readOptional(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):java.util.Optional<$dtoType> {")
        out.tab(1).println("return readOptional(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        out.tab(1)
            .println("override fun rxReadOptional(query:org.jooq.Query):io.reactivex.Single<java.util.Optional<$dtoType>> {")
        out.tab(1).println("return rxReadOptional(query, $fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("override fun rxReadOptional(query:org.jooq.Query,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<java.util.Optional<$dtoType>> {")
        out.tab(1).println("return rxReadOptional(query, $fullJavaTableName, client)")
        out.tab(1).println("}")

        if (!readonly) {
            out.tab(1).println("suspend fun update(dto:$dtoType, condition:org.jooq.Condition):Int {")
            val mapperGetter = "${dtoMapper(mapperPackage, mappers, dtoType)}($fullJavaTableName)"
            printMapper(out, mapperGetter)
            out.tab(2).println("return query(dsl.update($fullJavaTableName).set(map).where(condition)).rowCount()")
            out.tab(1).println("}")

            out.tab(1).println("fun rxUpdate(dto:$dtoType, condition:org.jooq.Condition):io.reactivex.Single<Int> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return rxQuery(dsl.update($fullJavaTableName).set(map).where(condition)).map{ it.delegate.rowCount() }")
            out.tab(1).println("}")

            out.tab(1)
                .println("suspend fun update(dto:$dtoType, condition:org.jooq.Condition, client:io.vertx.reactivex.sqlclient.SqlClient):Int {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return query(dsl.update($fullJavaTableName).set(map).where(condition), client).rowCount()")
            out.tab(1).println("}")

            out.tab(1)
                .println("fun rxUpdate(dto:$dtoType, condition:org.jooq.Condition, client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<Int> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return rxQuery(dsl.update($fullJavaTableName).set(map).where(condition),client).map{ it.delegate.rowCount() }")
            out.tab(1).println("}")


            out.tab(1)
                .println("suspend fun updateReturning(dto:$dtoType, condition:org.jooq.Condition):List<$dtoType> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return map(query(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields())))")
            out.tab(1).println("}")

            out.tab(1)
                .println("suspend fun updateReturningOne(dto:$dtoType, condition:org.jooq.Condition):$dtoType {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return map(queryOne(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields())))")
            out.tab(1).println("}")


            out.tab(1)
                .println("fun rxUpdateReturning(dto:$dtoType, condition:org.jooq.Condition):io.reactivex.Single<List<$dtoType>> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return rxQuery(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields())).map{ map(it.delegate as io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>) }")
            out.tab(1).println("}")

            out.tab(1)
                .println("fun rxUpdateReturningOne(dto:$dtoType, condition:org.jooq.Condition):io.reactivex.Single<$dtoType> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return rxQueryOne(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields())).map{ map(it.delegate) }")
            out.tab(1).println("}")

            out.tab(1)
                .println("suspend fun updateReturning(dto:$dtoType, condition:org.jooq.Condition, client:io.vertx.reactivex.sqlclient.SqlClient):$dtoType {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return map(queryOne(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields()), client))")
            out.tab(1).println("}")

            out.tab(1)
                .println("fun rxUpdateReturning(dto:$dtoType, condition:org.jooq.Condition, client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<$dtoType> {")
            printMapper(out, mapperGetter)
            out.tab(2)
                .println("return rxQueryOne(dsl.update($fullJavaTableName).set(map).where(condition).returning(*$fullJavaTableName.fields()),client).map{ map(it.delegate) }")
            out.tab(1).println("}")

            out.tab(1).println("suspend fun create(dto:$dtoType):Int {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return query(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values)).rowCount()")
            out.tab(1).println("}")

            out.tab(1)
                .println("suspend fun create(dto:$dtoType,client:io.vertx.reactivex.sqlclient.SqlClient):Int {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return query(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values), client).rowCount()")
            out.tab(1).println("}")

            out.tab(1).println("suspend fun createReturning(dto:$dtoType):$dtoType {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return map(queryOne(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values).returning(*$fullJavaTableName.fields())))")
            out.tab(1).println("}")

            out.tab(1)
                .println("suspend fun createReturning(dto:$dtoType,client:io.vertx.reactivex.sqlclient.SqlClient):$dtoType {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return map(queryOne(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values).returning(*$fullJavaTableName.fields()), client))")
            out.tab(1).println("}")

            out.tab(1).println("fun rxCreate(dto:$dtoType):io.reactivex.Single<Int> {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return rxQuery(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values)).map{ it.delegate.rowCount() }")
            out.tab(1).println("}")

            out.tab(1)
                .println("fun rxCreate(dto:$dtoType,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<Int> {")
            out.tab(2)
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return rxQuery(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values), client).map{ it.delegate.rowCount() }")
            out.tab(1).println("}")

            out.tab(1).println("fun rxCreateReturning(dto:$dtoType):io.reactivex.Single<$dtoType> {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return rxQueryOne(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values).returning(*$fullJavaTableName.fields())).map{ map(it.delegate) }")
            out.tab(1).println("}")

            out.tab(1)
                .println("fun rxCreateReturning(dto:$dtoType,client:io.vertx.reactivex.sqlclient.SqlClient):io.reactivex.Single<$dtoType> {")
            printMapper(out, mapperGetter, true)
            out.tab(2)
                .println("return rxQueryOne(dsl.insertInto($fullJavaTableName).columns(map.keys).values(map.values).returning(*$fullJavaTableName.fields()), client).map{ map(it.delegate) }")
            out.tab(1).println("}")
        }
    }

    private fun printMapper(out: JavaWriter, mapperGetter: String, validate: Boolean = false) {
        if (validate) {
            out.tab(2).println("validate(dto)")
        }
        out.tab(2).println("val mapper = $mapperGetter")
        out.tab(2).println("val map = mapper.getValueMap(dto)")
    }

    private fun dtoMapper(mapperPackage: String, mappers: String, dtoType: String) =
        "$mapperPackage.$mappers.getMapper<$dtoType>"

    private fun generateMapperImpl(table: TableDefinition, out: JavaWriter) {
        val dtoType = out.ref(getStrategy().getFullJavaClassName(table, Mode.POJO))
        generateToDto(out, dtoType, table)
        generateGetValueMap(out, dtoType, table)
    }

    private fun generateToDto(
        out: JavaWriter,
        dtoType: String?,
        table: TableDefinition
    ) {
        val fullJavaTableName = getFullJavaTableName(table)
        out.tab(1)
            .println(
                "@SuppressWarnings(\"Duplicates\", \"unused\")\n" +
                    "override fun toDto(row:io.vertx.sqlclient.Row, offset:Int):$dtoType {"
            )

        out.tab(2).println("val dto = %s()", dtoType, dtoType)
        for ((pos, column) in table.columns.withIndex()) {
//      val pos = column.position - 1
            val offset = getOffset(pos)
//            out.tab(2).println("// converter: $column.type.converter")
            val setter = getStrategy().getJavaSetterName(column, Mode.INTERFACE)
            val columnType = getJavaType(column.type)
            val columnName = getStrategy().getJavaIdentifier(column)
            val javaMemberName = getJsonKeyName(column)
            val converter = column.type.converter
//            val attrName = getStrategy().getJavaMemberName(column)
//            val customJavaType = column.type.javaType
            val userType = column.type.userType
//            println("$columnName::$columnType::$userType")
            if (handleCustomDBTypeFromRow(table, column, setter, columnType, javaMemberName, pos, out)) {
                // handled by user
            } else if (handleJsonFromRow(table, column, setter, columnType, javaMemberName, pos, out)) {
                // handling of vertx json types
            } else if (handleGeometricDBTypeFromRow(table, column, setter, columnType, javaMemberName, pos, out)) {
                // custom geometric types (uses dto from api package instead of pg-client types)
            } else if (handleReactiveSqlClientTypeFromRow(column, setter, javaMemberName, pos, out)) {
                // handle types of reactive-pg-client
            } else {
                if (column.type.javaType != null && column.type.converter != null) {
                    // TODO: check if arrays of custom object are stored as json or _json
                    val isArray = userType.startsWith('_')
                    val spread = if (isArray) "*" else ""
                    if (userType == "json" || userType == "jsonb") {
                        val type = JsonObject::class.java.name
                        out.tab(2)
                            .println("val ${column.name} = row.get($type::class.java,$offset)")
                    } else if (userType == "_json" || userType == "_jsonb") {
                        out.tab(2)
                            .println("val ${column.name} = row.get(Array<io.vertx.core.json.JsonObject>::class.java,$offset)")
                    } else {
                        val accessName = getSqlClientAccessName(column.definedType)
                        out.tab(2)
                            .println("val ${column.name} = row.get$accessName($offset)")
                    }
                    out.tab(2).println("if (${column.name} != null) {")
                    out.tab(3)
                        .println("val ${column.name}_converted=($fullJavaTableName.$columnName.converter as $converter).from(${column.name})")
                    out.tab(3).println("if(${column.name}_converted != null) {")
                    out.tab(4).println("dto.$setter($spread${column.name}_converted)")
                    out.tab(3).println("}")
                    out.tab(2).println("}")
                } else {
                    LOG.warn(
                        String.format(
                            "Omitting unrecognized type %s (%s) for column %s in table %s!",
                            columnType,
                            column.type.userType,
                            column.name,
                            table.name
                        )
                    )
                    out.tab(2)
                        .println(
                            String.format(
                                "// Omitting unrecognized type %s for column %s!",
                                columnType,
                                column.name
                            )
                        )
                }
            }
        }
        out.tab(2).println("return dto")
        out.tab(1).println("}")
    }

    override fun getJavaType(type: DataTypeDefinition, udtMode: Mode): String {
        // use custom type if specified
        if (type.javaType != null) {
            return type.javaType
        }
        val geometricJavaType = getGeometricJavaType(type, udtMode)
        if (geometricJavaType != null) return geometricJavaType
        val pgClientType = getSqlClientType(type)
        if (pgClientType != null) {
            return pgClientType.name + getArrayPostfix(type)
        }
        return super.getJavaType(type, udtMode)
    }

    private fun getArrayPostfix(type: DataTypeDefinition): String {
        return if (type.userType.startsWith('_')) "[]" else ""
    }

    private fun getSqlClientAccessName(type: DataTypeDefinition): String {
        val pgClientType = getSqlClientType(type)
        val userType = type.userType
        val typeName = if (pgClientType != null) {
            pgClientType.simpleName
        } else when (userType.removePrefix("_")) {
            "char" -> String::class.java.simpleName
            "varchar" -> String::class.java.simpleName
            "text" -> String::class.java.simpleName
            "uuid" -> UUID::class.java.simpleName
            "float4" -> Float::class.java.simpleName
            "float8" -> Double::class.java.simpleName
            "int2" -> Short::class.java.simpleName
            "smallint" -> Short::class.java.simpleName
            "int4" -> Integer::class.java.simpleName
            "integer" -> Integer::class.java.simpleName
            "int8" -> Long::class.java.simpleName
            "bigint" -> Long::class.java.simpleName
            "bool" -> Boolean::class.java.simpleName
            "name" -> String::class.java.simpleName
            else -> {
                val fqn = getJavaType(type)
                fqn.substring(fqn.lastIndexOf(".") + 1)
            }
        }
        return if (userType.startsWith("_")) typeName + "Array" else typeName
    }

    private fun getSqlClientType(type: DataTypeDefinition): Class<*>? {
        return when (val userType = type.userType.removePrefix("_")) {
            // basic types
            "bytea" -> Buffer::class.java
            "numeric" -> Numeric::class.java
//            "char" -> String::class.java
//            "varchar" -> String::class.java
//            "text" -> String::class.java
//            "uuid" -> UUID::class.java
//            "float4" -> Float::class.java
//            "float8" -> Double::class.java
//            "int2" -> Short::class.java
//            "smallint" -> Short::class.java
//            "int4" -> Integer::class.java
//            "integer" -> Integer::class.java
//            "int8" -> Long::class.java
//            "bigint" -> Long::class.java
//            "bool" -> Boolean::class.java
//            "name" -> String::class.java

            // date + time
            "date" -> LocalDate::class.java
            "time" -> LocalTime::class.java
            "timetz" -> OffsetTime::class.java
            "timestamp" -> LocalDateTime::class.java
            "timestamptz" -> OffsetDateTime::class.java

            // json
//            "json" -> JsonObject::class.java
//            "jsonb" -> JsonObject::class.java
//            "_json" -> JsonArray::class.java
//            "_jsonb" -> JsonArray::class.java

            // geometric types
            "point" -> Point::class.java
            "line" -> Line::class.java
            "lseg" -> LineSegment::class.java
            "box" -> Box::class.java
            "polygon" -> Polygon::class.java
            "path" -> Path::class.java
            "circle" -> Circle::class.java

            "interval" -> Interval::class.java

            "serial2" -> Short::class.java
            "serial4" -> Integer::class.java
            "serial8" -> Long::class.java

            else -> {
                LOG.debug("Unhandled type $userType, returning null")
                return null
            }
        }
    }

    open fun getGeometricJavaType(type: DataTypeDefinition, udtMode: Mode): String? {
        return when (type.userType) {
            "point" -> PointDTO::class.java.name
            "_point" -> PointDTO::class.java.name + "[]"
            "line" -> LineDTO::class.java.name
            "_line" -> LineDTO::class.java.name + "[]"
            "lseg" -> LineSegmentDTO::class.java.name
            "_lseg" -> LineSegmentDTO::class.java.name + "[]"
            "box" -> BoxDTO::class.java.name
            "_box" -> BoxDTO::class.java.name + "[]"
            "polygon" -> PolygonDTO::class.java.name
            "_polygon" -> PolygonDTO::class.java.name + "[]"
            "path" -> PathDTO::class.java.name
            "_path" -> PathDTO::class.java.name + "[]"
            "circle" -> CircleDTO::class.java.name
            "_circle" -> CircleDTO::class.java.name + "[]"

            "interval" -> IntervalDTO::class.java.name
            "_interval" -> IntervalDTO::class.java.name + "[]"
            else -> null
        }
    }

    private fun getOffset(pos: Int) = if (pos == 0) "offset" else "offset+$pos"

    override fun setStrategy(strategy: GeneratorStrategy) {
        require(strategy is TablePrefixAwareGeneratorStrategy) { "Requires instance of TablePrefixAwareGeneratorStrategy" }
        super.setStrategy(strategy)
        this.en4bleGeneratorStrategy = strategy
    }

    private fun getJsonKeyName(column: TypedElementDefinition<*>): String {
        return en4bleGeneratorStrategy.getJsonKeyName(column)
    }

    private fun isEnum(table: TableDefinition, column: TypedElementDefinition<*>): Boolean {
        return table.database.getEnum(
            table.schema,
            column.type.userType
        ) != null
    }

    private fun generateGetValueMap(
        out: JavaWriter,
        dtoType: String?,
        table: TableDefinition
    ) {
        out.tab(1)
            .println(
                "override fun getValueMap(o:Any):Map<org.jooq.Field<*>,*>  {"
            )
        out.tab(2).println("val dto = o as $dtoType")
        out.tab(2).println("val map = mutableMapOf<org.jooq.Field<*>,Any>()")
        for (column in table.columns) {
            val tableName = getStrategy().getFullJavaClassName(table) + "." + getStrategy().getJavaIdentifier(table)
            val columnName = getStrategy().getJavaIdentifier(column)
            val attrName = getStrategy().getJavaMemberName(column)
            out.tab(2).println("val $attrName=dto.$attrName")
            out.tab(2).println("if($attrName !=null) {")
            out.tab(3).println("map.put($tableName.$columnName,$attrName)")
            out.tab(2).println("}")
        }
        out.tab(2).println("return map")
        out.tab(1).println("}")
    }

    private fun handleReactiveSqlClientTypeFromRow(
        column: TypedElementDefinition<*>,
        setter: String,
        javaMemberName: String?,
        pos: Int,
        out: JavaWriter
    ): Boolean {
        // custom java type was specified (using custom converter)
        if (column.type.javaType != null) {
            return false
        }
        val userType = column.type.userType
        val offset = getOffset(pos)
        val typeName = when (userType) {
            "varchar" -> "String"
            "_varchar" -> "StringArray"
            "text" -> "String"
            "_text" -> "StringArray"
            "name" -> "String"
            "_name" -> "StringArray"

            // numeric types
            "numeric" -> "Numeric"
            "_numeric" -> "NumericArray"
            "int2" -> "Short"
            "_int2" -> "ShortArray"
            "int4" -> "Integer"
            "_int4" -> "IntegerArray"
            "int8" -> "Long"
            "_int8" -> "LongArray"
            "float4" -> "Float"
            "_float4" -> "FloatArray"
            "float8" -> "Double"
            "_float8" -> "DoubleArray"
            "bool" -> "Boolean"
            "_bool" -> "BooleanArray"
            "bytea" -> "Buffer"
            "_bytea" -> "BufferArray"

            // date + time
            "date" -> "LocalDate"
            "_date" -> "LocalDateArray"
            "time" -> "LocalTime"
            "_time" -> "LocalTimeArray"
            "timetz" -> "OffsetTime"
            "_timetz" -> "OffsetTimeArray"
            "timestamp" -> "LocalDateTime"
            "_timestamp" -> "LocalDateTimeArray"
            "timestamptz" -> "OffsetDateTime"
            "_timestamptz" -> "OffsetDateTimeArray"

            // json
//            "json" -> "Json"
//            "_json" -> "JsonArray"
//            "jsonb" -> "Json"
//            "_jsonb" -> "JsonArray"

            // geometric types
//            "point" -> "Point"
//            "_point" -> "PointArray"
//            "line" -> "Line"
//            "_line" -> "LineArray"
//            "lseg" -> "LineSegment"
//            "_lseg" -> "LineSegmentArray"
//            "box" -> "Box"
//            "_box" -> "BoxArray"
//            "path" -> "Path"
//            "_path" -> "PathArray"
//            "polygon" -> "Polygon"
//            "_polygon" -> "PolygonArray"
//            "circle" -> "Circle"
//            "_circle" -> "CircleArray"

            "uuid" -> "UUID"
            "_uuid" -> "UUIDArray"
            "interval" -> "Interval"
            "_interval" -> "IntervalArray"

            "serial2" -> "Short"
            "serial4" -> "Integer"
            "serial8" -> "Long"

            else -> null
        }

        if (typeName != null) {
            if (userType.startsWith("_")) {
                out.tab(2).println("val $javaMemberName = row.get$typeName($offset)")
                out.tab(2).println("if($javaMemberName != null) {")
                out.tab(3).println("dto.$setter(*$javaMemberName)")
                out.tab(2).println("}")
            } else {
                out.tab(2).println("dto.$setter(row.get$typeName($offset))")
            }
            return true
//
//            val a = if (userType.startsWith('_')) "*" else ""
//            out.tab(2).println("dto.$setter(${a})")
//            return true
        }
        return false
    }

    /**
     * Uses custom geometric types found in api package instead of pg-client types.
     * This allows us to decouple db code from the api specs and adds OpenApi documentation to types.
     *
     * Override this to use your own implementation
     * or leave it empty, returning false to  use the default pg-client types.
     */
    open fun handleGeometricDBTypeFromRow(
        table: TableDefinition,
        column: TypedElementDefinition<*>,
        setter: String,
        columnType: String?,
        javaMemberName: String?,
        pos: Int,
        out: JavaWriter
    ): Boolean {
        // custom java type was specified (using custom converter)
        if (column.type.javaType != null) {
            return false
        }
        val userType = column.type.userType
        val offset = getOffset(pos)
        val s = when (userType) {
            "point" -> "getPointDTO(row.get(io.vertx.pgclient.data.Point::class.java,$offset))"
            "_point" -> "getPointDTOs(row.getValues(io.vertx.pgclient.data.Point::class.java,$offset))"

            "line" -> "getLineDTO(row.get(io.vertx.pgclient.data.Line::class.java,$offset))"
            "_line" -> "getLineDTOs(row.getValues(io.vertx.pgclient.data.Line::class.java,$offset))"

            "lseg" -> "getLineSegmentDTO(row.get(io.vertx.pgclient.data.LineSegment::class.java,$offset))"
            "_lseg" -> "getLineSegmentDTOs(row.getValues(io.vertx.pgclient.data.LineSegment::class.java,$offset))"

            "box" -> "getBoxDTO(row.get(io.vertx.pgclient.data.Box::class.java,$offset))"
            "_box" -> "getBoxDTOs(row.getValues(io.vertx.pgclient.data.Box::class.java,$offset))"

            "path" -> "getPathDTO(row.get(io.vertx.pgclient.data.Path::class.java,$offset))"
            "_path" -> "getPathDTOs(row.getValues(io.vertx.pgclient.data.Path::class.java,$offset))"

            "polygon" -> "getPolygonDTO(row.get(io.vertx.pgclient.data.Polygon::class.java,$offset))"
            "_polygon" -> "getPolygonDTOs(row.getValues(io.vertx.pgclient.data.Polygon::class.java,$offset))"

            "circle" -> "getCircleDTO(row.get(io.vertx.pgclient.data.Circle::class.java,$offset))"
            "_circle" -> "getCircleDTOs(row.getValues(io.vertx.pgclient.data.Circle::class.java,$offset))"

            "interval" -> "getIntervalDTO(row.get(io.vertx.pgclient.data.Interval::class.java,$offset))"
            "_interval" -> "getIntervalDTOs(row.getValues(io.vertx.pgclient.data.Interval::class.java,$offset))"

            else -> null
        }

        if (s != null) {
            if (userType.startsWith("_")) {
                out.tab(2).println("val $javaMemberName = io.en4ble.pgaccess.util.JooqHelper.$s")
                out.tab(2).println("if($javaMemberName != null) {")
                out.tab(3).println("dto.$setter(*$javaMemberName)")
                out.tab(2).println("}")
            } else {
                out.tab(2).println("dto.$setter(io.en4ble.pgaccess.util.JooqHelper.$s)")
            }
            return true
        }
        return false
    }

    open fun handleJsonFromRow(
        table: TableDefinition,
        column: TypedElementDefinition<*>,
        setter: String,
        columnType: String?,
        javaMemberName: String?,
        pos: Int,
        out: JavaWriter
    ): Boolean {
        // custom java type was specified (using custom converter)
        if (column.type.javaType != null) {
            return false
        }
        val userType = column.type.userType
        val offset = getOffset(pos)
        val s = when (userType) {
            "json" -> "row.get(io.vertx.core.json.JsonObject::class.java,$offset)"
            "_json" -> "row.getValues(Array<io.vertx.core.json.JsonObject>::class.java,$offset)"

            "jsonb" -> "row.get(io.vertx.core.json.JsonObject::class.java,$offset)"
            "_jsonb" -> "row.getValues(Array<io.vertx.core.json.JsonObject>::class.java,$offset)"

            else -> null
        }

        if (s != null) {
            out.tab(2).println("dto.$setter($s)")
            return true
        }
        return false
    }

    open fun handleCustomDBTypeFromRow(
        table: TableDefinition,
        column: TypedElementDefinition<*>,
        setter: String,
        columnType: String?,
        javaMemberName: String?,
        pos: Int,
        out: JavaWriter
    ): Boolean {
//        val userType = column.type.userType
//        var s: String? = null
//        val offset = getOffset(pos)
//        when (userType) {
//            "geography" -> {
//                s = "io.en4ble.pgaccess.util.JooqHelper.INSTANCE.getPointDTO(row.getPoint,$offset))"
//            }
//        }
//
//        if (s != null) {
//            out.tab(2).println("dto.%s(%s);", setter, s)
//            return true
//        }
        return false
    }

    override fun generateRecordClassFooter(table: TableDefinition?, out: JavaWriter?) {
        // Do nothing
        // the orginal implementation writes out toJson and fromJson for records, which break our code
        // and we currently don't need it. fix the implementation if it is required.
    }

    protected open fun generateMappers(schema: SchemaDefinition) {
        LOG.info("Generating table mappers")
        for (table in schema.tables) {
            try {
                generateMapper(table)
            } catch (e: Exception) {
                LOG.error("Error while generating mapper for $table", e)
            }
        }
        generateMappersClass(schema)
    }

    private fun generateMappersClass(schema: SchemaDefinition) {
        val x = schema.tables[0]
        val packageDir = File(getFile(x, Mode.DEFAULT).parent, "mappers")
        val mappersClassName = getMappersClassName(schema)
        val file = File(packageDir, "$mappersClassName.kt")
        val out = newJavaWriter(file)
        LOG.info("Generating Mappers class: " + out.file().name)
        out.println("package " + getStrategy().getJavaPackageName(x) + ".mappers")
        out.println("object $mappersClassName {")
        out.tab(1)
            .println("private val mappers = mutableMapOf<org.jooq.Table<*>, io.en4ble.pgaccess.mappers.JooqMapper<*>>()")
        out.tab(1).println("init {")
        for (table in schema.tables) {
            val mapperName = getMapperName(table)
            val mapperPackage = getMapperPackage(table)
            val fullJavaTableName = getFullJavaTableName(table)
            out.tab(2).println("mappers[$fullJavaTableName] = $mapperPackage.$mapperName.instance()")
        }
        out.tab(1).println("}")

        out.tab(1).println("@Suppress(\"UNCHECKED_CAST\")")
        out.tab(1)
            .println("fun <T> getMapper(table:org.jooq.Table<*>):io.en4ble.pgaccess.mappers.JooqMapper<T> {")
        out.tab(2)
            .println("val mapper = mappers[table] ?: throw RuntimeException(\"No mapper for table \$table found!\")")
        out.tab(2).println("return mapper as io.en4ble.pgaccess.mappers.JooqMapper<T>")
        out.tab(1).println("}")
        out.println("}")
        closeJavaWriter(out)
    }

    private fun getFullJavaTableName(table: TableDefinition): String {
        val tableClass = getStrategy().getFullJavaClassName(table, Mode.DOMAIN)
        val tableName = getStrategy().getJavaIdentifier(table)
        return "$tableClass.$tableName"
    }

    private fun getMappersClassName(schema: SchemaDefinition) =
        "${getStrategy().getJavaClassName(schema)}Mappers"

    protected open fun generateMapper(table: TableDefinition) {
        val daoFile = getFile(table, Mode.DEFAULT)
        val pojoName = getPojoName(table)
        val mapperName = getMapperName(table)
        val file = File(File(daoFile.parent, "mappers"), "$mapperName.kt")
        val out = newJavaWriter(file)
        LOG.info("Generating Mapper for " + out.file().name)

        printClassJavadoc(out, "")
        val mapperPackage = getMapperPackage(table)
        out.println("package $mapperPackage")

        out.println("import " + getStrategy().getFullJavaClassName(table, Mode.POJO))

        out.println("@Suppress(\"PARAMETER_NAME_CHANGED_ON_OVERRIDE\", \"RemoveRedundantQualifierName\")")
        out.println("class $mapperName:io.en4ble.pgaccess.mappers.AbstractJooqMapper<$pojoName>() {")

        out.tab(1).println("companion object {")
        out.tab(2).println("private val instance = $mapperPackage.$mapperName()")
        out.tab(2).println("fun instance():$mapperName {")
        out.tab(3).println("return instance")
        out.tab(2).println("}")
        out.tab(2).println("fun map(row:io.vertx.sqlclient.Row):$pojoName {")
        out.tab(3).println("return instance.toDto(row)")
        out.tab(2).println("}")

        out.tab(2).println("fun map(res:io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>  ):List<$pojoName>  {")
        out.tab(3).println("return instance.toList(res)")
        out.tab(2).println("}")
        out.tab(1).println("}")

        generateMapperImpl(table, out)
        out.println("}")
        closeJavaWriter(out)
    }

    private fun getMapperPackage(table: TableDefinition) =
        getStrategy().getJavaPackageName(table) + ".mappers"

    private fun getPojoName(table: TableDefinition): String {
        return getStrategy().getJavaClassName(table, Mode.POJO)
    }

    private fun getMapperName(table: TableDefinition): String {
        return getPojoName(table) + "Mapper"
    }

    override fun generatePojos(schema: SchemaDefinition) {
        generateMappers(schema)
        return super.generatePojos(schema)
    }

    override fun generateDao(table: TableDefinition, out: JavaWriter) {
        val key = table.primaryKey
        val readonly = key == null
        val strategy = getStrategy()
        val className = strategy.getJavaClassName(table, Mode.DAO)
        val interfaces = out.ref(strategy.getJavaClassImplements(table, Mode.DAO))
        val tableRecord = out.ref(strategy.getFullJavaClassName(table, Mode.RECORD))
        val tableIdentifier = ref(strategy.getFullJavaIdentifier(table), 2)

        val pType = out.ref(strategy.getFullJavaClassName(table, Mode.POJO))

        val keyColumns = key?.keyColumns
        var tType = if (keyColumns == null) {
            Record::class.java.name
        } else when {
            keyColumns.size == 1 -> getJavaType(keyColumns[0].getType(resolver()), Mode.POJO)
            keyColumns.size <= Constants.MAX_ROW_DEGREE -> {
                var generics = ""
                var separator = ""

                for (column in keyColumns) {
                    generics += separator + out.ref(getJavaType(column.getType(resolver())))
                    separator = ", "
                }

                Record::class.java.name + keyColumns.size + "<" + generics + ">"
            }
            else -> Record::class.java.name
        }

        tType = ref(tType)

        out.println("@file:Suppress(\"RedundantSemicolon\",\"unused\", \"RemoveRedundantQualifierName\")")
        printPackage(out, table, Mode.DAO)
        generateDaoClassJavadoc(table, out)
        printClassAnnotations(out, table.schema)

        if (readonly) {
            out.println(
                "abstract class $className(dbContext:io.en4ble.pgaccess.DatabaseContext) : $readonlyDaoBaseClassFqn<$tableRecord, $pType>(dbContext, $tableIdentifier, $pType::class.java),${interfaces.joinToString()} {"
            )
        } else {
            out.println(
                "abstract class $className(dbContext:io.en4ble.pgaccess.DatabaseContext) : $updatableDaoBaseClassFqn<$tableRecord, $pType, $tType>(dbContext, $tableIdentifier, $pType::class.java),${interfaces.joinToString()} {"
            )
        }
        out.tab(1).println("override fun table(): org.jooq.Table<$tableRecord> {")
        out.tab(2).println("return $tableIdentifier")
        out.tab(1).println("}")

        // Template method implementations
        // -------------------------------

        val fieldMap = mutableMapOf<String, String>()

        for (column in table.columns) {
            val colName = column.outputName
            val colClass = strategy.getJavaClassName(column)
            val colTypeFull = getJavaType(column.getType(resolver()))
            var colType = out.ref(colTypeFull)
            if (colType == "Integer") {
                colType = "Int"
            }
            val colIdentifier = ref(strategy.getFullJavaIdentifier(column), colRefSegments(column))
            val attrName = strategy.getJavaMemberName(column)
            val fullJavaTableName = getFullJavaTableName(table)

            fieldMap[attrName] = colIdentifier!!

            // TODO: make name of update timestamp field  configurable
            if (attrName == "updated" && !readonly) {
                val updateTimestampField = "UPDATED"
                out.tab(1)
                    .println("override fun getUpdatedField(): org.jooq.TableField<$tableRecord, LocalDateTime> {")
                out.tab(2).println("return $tableIdentifier.$updateTimestampField")
                out.tab(1).println("}")
            }

            if ("java.lang.Object" != colTypeFull) { // don't generate readBy methods for unknown types
                // skip array types for now
                if (!colType.endsWith("[]")) {
                    // readBy[Column]([T]...)
                    // -----------------------
                    if (readonly && attrName == "id") {
                        // readOne methods are generated for all unique columns, but in a view there are no constraints
                        // so just generate it if the table has no keys and the current the column is named id
                        generateReadOneByMethods(out, colClass, colType, pType, colIdentifier, fullJavaTableName)
                    }

                    if (!printDeprecationIfUnknownType(out, colTypeFull))
                        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code>", colName)

                    out.tab(1).println("suspend fun readBy$colClass(vararg values:$colType):List<$pType> {")
                    out.tab(2).println("return read($colIdentifier.`in`(values.toList()),$fullJavaTableName)")
                    out.tab(1).println("}")
                    out.tab(1)
                        .println("fun rxReadBy$colClass(vararg values:$colType):io.reactivex.Single<List<$pType>> {")
                    out.tab(2).println("return rxRead($colIdentifier.`in`(values.toList()),$fullJavaTableName)")
                    out.tab(1).println("}")

                    out.tab(1)
                        .println("suspend fun readBy$colClass(vararg values:$colType, orderBy: List<io.en4ble.pgaccess.dto.OrderDTO>):List<$pType> {")
                    out.tab(2).println("return read($colIdentifier.`in`(values.toList()),$fullJavaTableName, orderBy)")
                    out.tab(1).println("}")
                    out.tab(1)
                        .println("fun rxReadBy$colClass(vararg values:$colType, orderBy: List<io.en4ble.pgaccess.dto.OrderDTO>):io.reactivex.Single<List<$pType>> {")
                    out.tab(2)
                        .println("return rxRead($colIdentifier.`in`(values.toList()),$fullJavaTableName, orderBy)")
                    out.tab(1).println("}")

                    out.tab(1)
                        .println("suspend fun readBy$colClass(vararg values:$colType, page: io.en4ble.pgaccess.dto.PagingDTO):List<$pType> {")
                    out.tab(2).println("return read($colIdentifier.`in`(values.toList()),$fullJavaTableName, page)")
                    out.tab(1).println("}")
                    out.tab(1)
                        .println("fun rxReadBy$colClass(vararg values:$colType, page: io.en4ble.pgaccess.dto.PagingDTO):io.reactivex.Single<List<$pType>> {")
                    out.tab(2).println("return rxRead($colIdentifier.`in`(values.toList()),$fullJavaTableName, page)")
                    out.tab(1).println("}")
                }
                // readOneBy[Column]([T])
                // -----------------------
                ukLoop@ for (uk in column.uniqueKeys) {

                    // If column is part of a single-column unique key...
                    if (uk.keyColumns.size == 1 && uk.keyColumns[0] == column) {
                        if (!printDeprecationIfUnknownType(out, colTypeFull))
                            out.tab(1).javadoc("Read a unique record that has <code>$colName = value</code>")

                        generateReadOneByMethods(out, colClass, colType, pType, colIdentifier, fullJavaTableName)

                        break@ukLoop
                    }
                }
            }
        }

        generateDbFieldMethod(out, fieldMap)
        generateDaoClassFooter(table, out)
        out.println("}")
    }

    private fun generateReadOneByMethods(
        out: JavaWriter,
        colClass: String?,
        colType: String?,
        pType: String?,
        colIdentifier: String?,
        fullJavaTableName: String
    ) {
        out.tab(1).println("suspend fun readOneBy$colClass(value:$colType):$pType {")
        out.tab(2).println("return readOne($colIdentifier.eq(value),$fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1).println("fun rxReadOneBy$colClass(value:$colType):io.reactivex.Single<$pType> {")
        out.tab(2).println("return rxReadOne($colIdentifier.eq(value),$fullJavaTableName)")
        out.tab(1).println("}")

        out.tab(1).println("suspend fun readOptionalBy$colClass(value:$colType):java.util.Optional<$pType> {")
        out.tab(2).println("return readOptional($colIdentifier.eq(value),$fullJavaTableName)")
        out.tab(1).println("}")
        out.tab(1)
            .println("fun rxReadOptionalBy$colClass(value:$colType):io.reactivex.Single<java.util.Optional<$pType>> {")
        out.tab(2).println("return rxReadOptional($colIdentifier.eq(value),$fullJavaTableName)")
        out.tab(1).println("}")
    }

    private fun generateDbFieldMethod(
        out: JavaWriter,
        fieldMap: Map<String, String>
    ) {

        out.tab(1).println("private val dbFieldMap =")
        out.tab(1).println("hashMapOf<String, org.jooq.Field<*>>(")
        var first = true
        fieldMap.forEach {
            if (first) {
                first = false
            } else {
                out.println(",")
            }
            out.tab(1).print("\"${it.key}\" to ${it.value}")
        }
        out.tab(1).println(")")

        out.tab(1).println("override fun getDbField(dtoField: String): org.jooq.Field<*> {")
        out.tab(1)
            .println("return dbFieldMap[dtoField] ?: throw javax.validation.ValidationException(\"Unknown field \$dtoField\")")
        out.tab(1).println("}")
    }

    private fun printDeprecationIfUnknownType(out: JavaWriter, type: String): Boolean {
        return printDeprecationIfUnknownType(out, type, 1)
    }

    @Suppress("SameParameterValue")
    private fun printDeprecationIfUnknownType(out: JavaWriter, type: String, indentation: Int): Boolean {
        return if (generateDeprecationOnUnknownTypes() && "java.lang.Object" == type) {
            out.tab(indentation).javadoc(
                "@deprecated Unknown data type. "
                    + "Please define an explicit {@link org.jooq.Binding} to specify how this "
                    + "type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} "
                    + "in your code generator configuration."
            )
            out.tab(indentation).println("@java.lang.Deprecated")
            true
        } else {
            false
        }
    }

    /**
     * Get a reference to a [Class].
     */
    fun ref(clazz: Class<*>?): String? {
        return if (clazz == null) null else ref(clazz.name)
    }

    /**
     * Get a reference to a [Class].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun ref(clazzOrId: String?): String? {
        return if (clazzOrId == null) null else ref(listOf(clazzOrId), 1)[0]
    }

    /**
     * Get a reference to a list of [Class].
     */
    fun ref(clazzOrId: Array<String>?): Array<String> {
        return if (clazzOrId == null) emptyArray() else ref(listOf(*clazzOrId), 1).toTypedArray()
    }

    /**
     * Get a reference to a list of [Class].
     *
     *
     * Subtypes may override this to generate import statements.
     */
    fun ref(clazzOrId: List<String>?): List<String> {
        return if (clazzOrId == null) emptyList() else ref(clazzOrId, 1)
    }

    /**
     * Get a reference to a [Class].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun ref(clazzOrId: String?, keepSegments: Int): String? {
        return if (clazzOrId == null) null else ref(listOf(clazzOrId), keepSegments)[0]
    }

    /**
     * Get a reference to a list of [Class].
     */
    protected fun ref(clazzOrId: Array<String>?, keepSegments: Int): Array<String> {
        return if (clazzOrId == null) emptyArray() else ref(listOf(*clazzOrId), keepSegments).toTypedArray()
    }

    /**
     * Get a reference to a list of [Class].
     *
     *
     * Subtypes may override this to generate import statements.
     */
    @Suppress("UNUSED_PARAMETER")
    protected fun ref(clazzOrId: List<String>?, keepSegments: Int): List<String> {
        return clazzOrId ?: emptyList()
    }

    /**
     * Copied from JavaGenerator
     * @param column
     * @return
     */
    private fun colRefSegments(column: TypedElementDefinition<*>?): Int {
        if (column != null && column.container is UDTDefinition)
            return 2

        return if (!getStrategy().instanceFields) 2 else 3
    }

    override fun newJavaWriter(file: File): JavaWriter {
        return JavaWriter(file, generateFullyQualifiedTypes(), targetEncoding, generateJavadoc())
    }

    override fun getFile(definition: Definition): File {
        return fixExtension(definition)
    }

    override fun getFile(definition: Definition, mode: Mode): File {
        return fixExtension(definition, mode)
    }

    private fun fixExtension(definition: Definition, mode: Mode? = null): File {
        var file = if (mode == null) {
            getStrategy().getFile(definition)
        } else {
            getStrategy().getFile(definition, mode)
        }
        if (mode == Mode.DAO) {
            file = File(file.parentFile, file.name.replace(".java", ".kt"))
        }
        return file
    }

    override fun printPackage(out: JavaWriter, definition: Definition, mode: Mode) {
        printPackageComment(out, definition, mode)
        out.println("package %s;", getStrategy().getJavaPackageName(definition, mode))
        out.println()
        out.printImports()
        out.println()
    }

    override fun printClassAnnotations(out: JavaWriter, schema: SchemaDefinition?, catalog: CatalogDefinition?) {
        // NOOP
    }

    override fun generateGeneratedAnnotation(): Boolean {
        return false
    }

    /**
     * JavaWriter adds import java.lang.xxxx statements which create issues with kotlin, so we have to remove them.
     * (The writer can currently not be extended to achieve the same, so we have to use this slightliy dirty approach)
     */
    override fun closeJavaWriter(out: JavaWriter) {
        super.closeJavaWriter(out)
        val sb = StringBuffer()
        out.file().forEachLine {
            if (!it.startsWith("import java.lang")) {
                sb.append(it).append('\n')
            }
        }
        out.file().writeText(sb.toString())
    }
}
