package io.en4ble.pgaccess

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
open class DatabaseSettings(
    var host: String = "localhost",
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
    constructor() : this("", "", 0, "", 0, "", "")

    val url: String
        get() = if (ssl) {
            "jdbc:postgresql://$host:$port/$database?sslmode=require&currentSchema=$schema"
        } else {
            "jdbc:postgresql://$host:$port/$database?currentSchema=$schema"
        }
}
