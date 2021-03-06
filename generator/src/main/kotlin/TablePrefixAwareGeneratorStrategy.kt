package io.en4ble.pgaccess.generator

import com.fasterxml.jackson.databind.ObjectMapper
import io.en4ble.pgaccess.generator.JooqGeneratorUtils.getColumnNameWithoutTablePrefix
import io.en4ble.pgaccess.generator.JooqGeneratorUtils.getTablePrefix
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.*
import org.jooq.tools.StringUtils
import java.io.File
import java.io.Serializable

/** @author Mark Hofmann (mark@en4ble.io)
 */
open class TablePrefixAwareGeneratorStrategy : VertxGeneratorStrategy() {

    private val interfaceMappings by lazy {
        val projectDir = System.getProperty("projectDir")
//        println("####### $projectDir")
        val file = File("$projectDir/jooq/", "interfaces.json")
//        println("File: ${file.absolutePath}")
        val jsonString = if (file.exists()) file.readText() else null
//        println("json: $jsonString")
        if (jsonString != null) {
            val mapper = ObjectMapper()
            mapper.readValue(jsonString, Interfaces::class.java).list?.map { it.table to it.type }?.toMap()
                ?: emptyMap()
        } else {
            emptyMap()
        }
    }

    /**
     * Override this to specifiy what identifiers in Java should look like. This will just take the
     * identifier as defined in the database.
     */
    override fun getJavaIdentifier(definition: Definition): String {
//    val definitionPath = definition.definitionPath
        //    System.out.print(definition.getOutputName() + " - path: ");
        //    for (Definition d : definitionPath) {
        //      System.out.print(d.getOutputName() + ".");
        //    }
        //    System.out.println();

        return getCustomOutputName(definition).toUpperCase()
        //        return StringUtils.toCamelCase(getCustomOutputName(definition));
    }

    private fun isTable(definition: Definition): Boolean {
        return definition.definitionPath.size == 2
    }

    private fun isColumn(definition: Definition): Boolean {
        return definition.definitionPath.size == 3
    }

    private fun getTable(definition: Definition): Definition {
        return definition.definitionPath[1]
    }

    private fun getCustomOutputName(definition: Definition): String {
        var outputName = definition.outputName
        val index = outputName.indexOf('_')
        if (definition is UniqueKeyDefinition) {
            // avoid name collisions if the same (foreign) key name is used in multiple tables
            outputName = getTablePrefix(definition.table.name) + "_" + outputName
        } else {
            if (isColumn(definition)) {
                //      System.out.println("definition is column: " + outputName);
                val table = getTable(definition)
                outputName = getColumnNameWithoutTablePrefix(table.outputName, outputName)
            } else {
                //      System.out.println("definition is table: " + outputName);
                if (index == 1 || index == 2) {
                    outputName = outputName.substring(index + 1)
                }
            }
        }
        return outputName
    }

    private fun getTableName(definition: Definition): String {
        return getCustomOutputName(definition)
    }

    /**
     * Override these to specify what a setter in Java should look like. Setters are used in
     * TableRecords, UDTRecords, and POJOs. This example will name setters "set[NAME_IN_DATABASE]"
     */
    override fun getJavaSetterName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        var outputName = getCustomOutputName(definition)
        outputName = StringUtils.toCamelCase(outputName)
        return "set$outputName"
    }

    /** Just like setters...  */
    override fun getJavaGetterName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        var outputName = getCustomOutputName(definition)
        outputName = StringUtils.toCamelCase(outputName)
        return "get$outputName"
    }

    /**
     * Override this method to define what a Java method generated from a database Definition should
     * look like. This is used mostly for convenience methods when calling stored procedures and
     * functions. This example shows how to set a prefix to a CamelCase version of your procedure
     */
    override fun getJavaMethodName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return "call" + StringUtils.toCamelCase(getCustomOutputName(definition))
    }

    /**
     * Override this method to define how your Java classes and Java files should be named. This
     * example applies no custom setting and uses CamelCase versions instead
     */
    override fun getJavaClassName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        val name = getFixedJavaClassName(definition)

        return name ?: getJavaClassName0(definition, mode)
    }

    private fun getJavaClassName0(definition: Definition, mode: GeneratorStrategy.Mode): String {
        val result = StringBuilder()
        val outputName = getCustomOutputName(definition)
        // [#4562] Some characters should be treated like underscore
        result.append(
            StringUtils.toCamelCase(
                outputName.replace(' ', '_').replace('-', '_').replace('.', '_')
            )
        )

        if (mode == GeneratorStrategy.Mode.RECORD) {
            result.append("Record")
        } else if (mode == GeneratorStrategy.Mode.DAO) {
            result.append("DaoBase")
        } else if (mode == GeneratorStrategy.Mode.POJO) {
            result.append("Dto")
        } else if (mode == GeneratorStrategy.Mode.INTERFACE) {
            result.insert(0, "I")
        }

        return result.toString()
    }

    /** Override this method to re-define the package names of your generated artefacts.  */
    override fun getJavaPackageName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return super.getJavaPackageName(definition, mode)
    }

    /**
     * Override this method to define how Java members should be named. This is used for POJOs and
     * method arguments
     */
    override fun getJavaMemberName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return StringUtils.toCamelCaseLC(getCustomOutputName(definition))
    }

    override fun getJavaMemberName(definition: Definition): String {
        return getJavaMemberName(definition, GeneratorStrategy.Mode.DEFAULT)
    }

    override fun getJsonKeyName(column: TypedElementDefinition<*>): String {
        return getJavaMemberName(column)
    }

    /**
     * Override this method to define the base class for those artefacts that allow for custom base
     * classes
     */
    override fun getJavaClassExtends(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return Any::class.java.name
    }

    /**
     * Override this method to define the interfaces to be implemented by those artefacts that allow
     * for custom interface implementation
     */
    override fun getJavaClassImplements(definition: Definition, mode: GeneratorStrategy.Mode): List<String> {
        return if (mode == GeneratorStrategy.Mode.POJO) {
            val interfaceDef = interfaceMappings[definition.name]
//            println("############ ")
//            println(interfaceMappings.values)
//            println("## ${definition.name}")
//            println("############ ")
            val interfaces = mutableListOf(
                Serializable::class.java.name, Cloneable::class.java.name
            )
            if (interfaceDef != null) {
                interfaces.add(interfaceDef)
            }
            interfaces
        } else listOf(Serializable::class.java.name, Cloneable::class.java.name)
    }

    /**
     * Override this method to define the suffix to apply to routines when they are overloaded.
     *
     *
     * Use this to resolve compile-time conflicts in generated source code, in case you make heavy
     * use of procedure overloading
     */
    override fun getOverloadSuffix(
        definition: Definition,
        mode: GeneratorStrategy.Mode,
        overloadIndex: String
    ): String {
        return "_OverloadIndex_$overloadIndex"
    }

    private fun getFixedJavaClassName(definition: Definition): String? {

        // [#2032] Intercept default catalog
        return if (definition is CatalogDefinition && definition.isDefaultCatalog) {
            "DefaultCatalog"
        } else if (definition is SchemaDefinition && definition.isDefaultSchema) {
            "DefaultSchema"
        } else {
            null
        } // [#2089] Intercept default schema
    }

    private data class Interfaces(val list: List<InterfaceMapping>? = null)
    private data class InterfaceMapping(val table: String? = null, val type: String? = null)
}
