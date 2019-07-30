package io.en4ble.pgaccess.converters

import io.en4ble.pgaccess.dto.PointDTO
import org.jooq.Binding
import org.jooq.BindingGetResultSetContext
import org.jooq.BindingGetSQLInputContext
import org.jooq.BindingGetStatementContext
import org.jooq.BindingRegisterContext
import org.jooq.BindingSQLContext
import org.jooq.BindingSetSQLOutputContext
import org.jooq.BindingSetStatementContext
import org.jooq.Converter
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException

/**
 * Database binder, required to tell Postgresql which type to use for Point(DTO)
 * @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
class PostgresPointBinding : Binding<Any, PointDTO> {
    private val converter = PointDTOConverter()

    override fun converter(): Converter<Any, PointDTO> {
        return converter
    }

    @Throws(SQLException::class)
    override fun sql(ctx: BindingSQLContext<PointDTO>) {
        // this change is key here. I suspect the manual was misleading...
        ctx.render().visit(DSL.sql("?::point"))
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<PointDTO>) {
        ctx.statement().setObject(ctx.index(), ctx.convert<Any>(converter).value())
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<PointDTO>) {
        ctx.convert<Any>(converter).value(ctx.resultSet().getObject(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<PointDTO>) {
        ctx.convert<Any>(converter).value(ctx.statement().getObject(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun get(bindingGetSQLInputContext: BindingGetSQLInputContext<PointDTO>) {
        throw SQLFeatureNotSupportedException()
    }

    @Throws(SQLException::class)
    override fun set(bindingSetSQLOutputContext: BindingSetSQLOutputContext<PointDTO>) {
        throw SQLFeatureNotSupportedException()
    }

    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<PointDTO>) {
        throw SQLFeatureNotSupportedException()
    }
}
