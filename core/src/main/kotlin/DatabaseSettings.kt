package io.en4ble.pgaccess

/** @author Mark Hofmann (mark@en4ble.io)
 */
@Suppress("unused")
open class DatabaseSettings(
    var host: String = "localhost",
    var database: String = "postgres",
    var port: Int = 5432,
    var schema: String = "public",
    var maxPoolSize: Int = 10,
    var username: String = "postgres",
    var password: String = "postgres"
) {
    constructor() : this("", "", 0, "", 0, "", "")

    val url: String
        get() = "jdbc:postgresql://$host:$port/$database?currentSchema=$schema"
}
