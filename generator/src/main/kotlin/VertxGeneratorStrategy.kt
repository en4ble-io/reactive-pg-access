package io.en4ble.pgaccess.generator

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.Definition
import org.jooq.meta.TypedElementDefinition
import java.io.File

/**
 * Created by jensklingsporn on 08.02.18.
 */
open class VertxGeneratorStrategy constructor(private val delegate: GeneratorStrategy = DefaultGeneratorStrategy()) :
    GeneratorStrategy {

    open fun getJsonKeyName(column: TypedElementDefinition<*>): String {
        return column.name
    }

    override fun getJavaClassImplements(definition: Definition, mode: GeneratorStrategy.Mode): List<String> {
        return delegate.getJavaClassImplements(definition, mode)
    }

    override fun getFileName(definition: Definition): String {
        return delegate.getFileName(definition)
    }

    override fun getFileName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getFileName(definition, mode)
    }

    override fun getFileRoot(): File {
        return delegate.fileRoot
    }

    override fun getFile(definition: Definition): File {
        return delegate.getFile(definition)
    }

    override fun getFile(definition: Definition, mode: GeneratorStrategy.Mode): File {
        return delegate.getFile(definition, mode)
    }

    override fun getFile(fileName: String): File {
        return delegate.getFile(fileName)
    }

    override fun getFileHeader(definition: Definition): String {
        return delegate.getFileHeader(definition)
    }

    override fun getFullJavaIdentifier(definition: Definition): String {
        return delegate.getFullJavaIdentifier(definition)
    }

    override fun getJavaSetterName(definition: Definition): String {
        return delegate.getJavaSetterName(definition)
    }

    override fun getJavaGetterName(definition: Definition): String {
        return delegate.getJavaGetterName(definition)
    }

    override fun getJavaMethodName(definition: Definition): String {
        return delegate.getJavaMethodName(definition)
    }

    override fun getJavaClassExtends(definition: Definition): String {
        return delegate.getJavaClassExtends(definition)
    }

    override fun getJavaClassImplements(definition: Definition): List<String> {
        return delegate.getJavaClassImplements(definition)
    }

    override fun getJavaClassName(definition: Definition): String {
        return delegate.getJavaClassName(definition)
    }

    override fun getJavaPackageName(definition: Definition): String {
        return delegate.getJavaPackageName(definition)
    }

    override fun getJavaMemberName(definition: Definition): String {
        return delegate.getJavaMemberName(definition)
    }

    override fun getFullJavaClassName(definition: Definition): String {
        return delegate.getFullJavaClassName(definition)
    }

    override fun getFullJavaClassName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getFullJavaClassName(definition, mode)
    }

    override fun getJavaIdentifiers(definitions: Collection<Definition>): List<String> {
        return delegate.getJavaIdentifiers(definitions)
    }

    override fun getJavaIdentifiers(vararg definitions: Definition): List<String> {
        return delegate.getJavaIdentifiers(*definitions)
    }

    override fun getFullJavaIdentifiers(definitions: Collection<Definition>): List<String> {
        return delegate.getFullJavaIdentifiers(definitions)
    }

    override fun getFullJavaIdentifiers(vararg definitions: Definition): List<String> {
        return delegate.getFullJavaIdentifiers(*definitions)
    }

    override fun setInstanceFields(instanceFields: Boolean) {
        delegate.instanceFields = instanceFields
    }

    override fun getInstanceFields(): Boolean {
        return delegate.instanceFields
    }

    override fun setJavaBeansGettersAndSetters(javaBeansGettersAndSetters: Boolean) {
        delegate.javaBeansGettersAndSetters = javaBeansGettersAndSetters
    }

    override fun getJavaBeansGettersAndSetters(): Boolean {
        return delegate.javaBeansGettersAndSetters
    }

    override fun getTargetDirectory(): String {
        return delegate.targetDirectory
    }

    override fun setTargetDirectory(directory: String) {
        delegate.targetDirectory = directory
    }

    override fun getTargetPackage(): String {
        return delegate.targetPackage
    }

    override fun setTargetPackage(packageName: String) {
        delegate.targetPackage = packageName
    }

    override fun getFileHeader(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getFileHeader(definition, mode)
    }

    override fun getJavaIdentifier(definition: Definition): String {
        return delegate.getJavaIdentifier(definition)
    }

    override fun getJavaSetterName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaSetterName(definition, mode)
    }

    override fun getJavaGetterName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaGetterName(definition, mode)
    }

    override fun getJavaMethodName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaMethodName(definition, mode)
    }

    override fun getJavaClassExtends(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaClassExtends(definition, mode)
    }

    override fun getJavaClassName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaClassName(definition, mode)
    }

    override fun getJavaPackageName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaPackageName(definition, mode)
    }

    override fun getJavaMemberName(definition: Definition, mode: GeneratorStrategy.Mode): String {
        return delegate.getJavaMemberName(definition, mode)
    }

    override fun getOverloadSuffix(
        definition: Definition,
        mode: GeneratorStrategy.Mode,
        overloadIndex: String
    ): String {
        return delegate.getOverloadSuffix(definition, mode, overloadIndex)
    }
}
