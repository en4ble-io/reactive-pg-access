package io.en4ble.examples.util

import io.en4ble.pgaccess.DatabaseConfig
import liquibase.Contexts
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jooq.Schema
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

/** @author Mark Hofmann (mark@en4ble.io)
 */
object LiquibaseUpdater {
    private val LOG by lazy { LoggerFactory.getLogger(LiquibaseUpdater::class.java) }
    fun updateDatabase(schema: Schema?, config: DatabaseConfig) {
        if (schema == null) {
            return
        }
        val schemaName = schema.name
        LOG.info("Starting liquibase update for schema: {}", schemaName)
        var conn: Connection? = null
        try {
            val url = getJdbcUrl(config, schema)
            val props = Properties()
            props.setProperty("user", config.username)
            props.setProperty("password", config.password)
            props.setProperty("ssl", "false")
            conn = DriverManager.getConnection(url, props)
            //            val logger = Slf4JLoggerFactory.LoggerFactory.getLogger("liquibase")
            //            (logger as ch.qos.logback.classic.Logger).level = Level.ERROR
            val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(conn))
            val liquibase = liquibase.Liquibase(
                "db/$schemaName/master.yaml", ClassLoaderResourceAccessor(), database
            )

            liquibase.update(null as Contexts?)
        } catch (e: Exception) {
            LOG.error("error running liquibase", e)
        } finally {
            if (conn != null) {
                try {
                    conn.close()
                } catch (e: Exception) {
                    LOG.error("couldn't close connection", e)
                }
            }
        }
        LOG.info("Completed liquibase update for schema: {}", schemaName)
    }

    private fun getJdbcUrl(config: DatabaseConfig, schema: Schema): String {
        return ("jdbc:postgresql://"
            + config.host
            + ":"
            + config.port
            + "/"
            + config.database
            + "?currentSchema="
            + schema.name)
    }
}
