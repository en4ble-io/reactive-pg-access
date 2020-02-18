package io.en4ble.pgaccess

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
data class DatabaseConfig(
    var host: String = "localhost",
    /**
     * In order to connect to the database via a backend pooled connection as on DigitalOcean,
     * use the name of the pool instead of the database
     */
    var database: String = "postgres",
    var port: Int = 5432,
    /** Please note that the schema is already hardcoded in the generated jOOQ classes. This property is used to pass
     * the schema name to custom code, like a liquibase updater or in cases where you have common database classes. */
    var schema: String = "public",
    var maxPoolSize: Int = 10,
    var username: String = "postgres",
    var password: String = "postgres",
    var ssl: Boolean = false
) {
    val url: String
        get() = "jdbc:postgresql://$host:$port/$database"
    // setting the current schema via the connection url uses SET search_path which is
    // not supported by the PGbouncer connection pool (as used by the DigitalOcean database cluser)
//     get() = "jdbc:postgresql://$host:$port/$database?currentSchema=$schema"
}
