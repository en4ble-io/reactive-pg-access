package io.en4ble.pgaccess.generator

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
object JooqGeneratorUtils {
    fun getColumnNameWithoutTablePrefix(tableName: String, columnName: String): String {
        val tablePrefix = getTablePrefix(tableName)
        // remove the table prefix
        if (columnName.startsWith(tablePrefix + "_")) {
            return columnName.substring(tablePrefix.length + 1)
        }
        return columnName
    }

    private fun getTablePrefix(tableName: String): String {
        val tablePrefix = StringBuilder()
        val chars = tableName.toCharArray()
        for (i in chars.indices) {
            val c = chars[i]
            if (c == '_' && i < chars.size - 1) {
                tablePrefix.append(chars[i + 1])
            }
        }
        return tablePrefix.toString()
    }
}
