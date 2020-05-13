package io.en4ble.pgaccess.exceptions

import org.slf4j.LoggerFactory

/** @author Mark Hofmann (mark@en4ble.io)
 */
class NoResultsException(sql: String) : RuntimeException("No results found for query, see log for details") {
    private val LOG = LoggerFactory.getLogger(NoResultsException::class.java)

    init {
        LOG.info("No results found for query {}", sql)
    }
}
