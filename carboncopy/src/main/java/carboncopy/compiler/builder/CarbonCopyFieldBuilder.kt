package carboncopy.compiler.builder

import carboncopy.annotations.CarbonCopy
import carboncopy.annotations.CarbonCopyAccessor
import carboncopy.annotations.CarbonCopyRename
import com.google.auto.common.MoreElements
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

/**
 * Adds a field and its gettter and setter to the [TypeSpec.Builder] class builder
 */
class CarbonCopyFieldBuilder(private val bindingManager: BindingManager, private val classBuilder: TypeSpec.Builder, private val field: Element) {

    private val name = getFieldName(field)
    private val type = getFieldType(field)
    private val methodNameSuffix = name[0].toUpperCase() + if (name.length > 1) name.substring(1) else ""
    private val originalName = field.simpleName
    private val originalMethodNameSuffix = originalName[0].toUpperCase() + if (originalName.length > 1) originalName.substring(1) else ""


    /**
     * Builds the field data
     */
    fun build() {
        classBuilder.addField(type, name, Modifier.PRIVATE)
        addGetter(classBuilder, name, type)
        addSetter(classBuilder, name, type)

        addSourceToCopyConversion()
        addCopyToSourceConversion()
    }

    /**
     * Add the getter method for the given field
     */
    private fun addGetter(classBuilder: TypeSpec.Builder, name: String, type: TypeName) {
        classBuilder.addMethod(MethodSpec.methodBuilder("get$methodNameSuffix")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Returns $name \n")
                .returns(type)
                .addStatement("return $name").build())

    }

    /**
     * Add the setter method for the given field
     */
    private fun addSetter(classBuilder: TypeSpec.Builder, name: String, type: TypeName) {
        classBuilder.addMethod(MethodSpec.methodBuilder("set$methodNameSuffix")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Sets $name \n")
                .addParameter(type, name)
                .addStatement("this.$name = $name").build())
    }

    /**
     * Add the statements for the source -> copy converter for this field
     */
    private fun addSourceToCopyConversion() {
        val methodBuilder = bindingManager.sourceToCopyMethodBuilder
        if (methodBuilder != null) {
            val accessorName = getSourceAccessor(true)
            if (accessorName != null) {
                var accessorMethodName = "source.$accessorName()"
                val type = TypeName.get(field.asType())
                if (!type.isPrimitive) {
                    val fieldElement = bindingManager.getElement(type.toString())
                    val ccAnnotation = fieldElement.getAnnotation(CarbonCopy::class.java)
                    if (ccAnnotation != null) {
                        accessorMethodName = "convert(source.$accessorName())"
                    }
                }
                methodBuilder.addStatement("copy.set$methodNameSuffix($accessorMethodName)")
            } else {
                methodBuilder.addStatement("\$T.copyField(source, \"$originalName\", copy, \"$name\")", ClassName.get("carboncopy.annotations", "CarbonCopyUtil"))
            }
        }
    }

    /**
     * Add the statements for the copy -> source converter for this field
     */
    private fun addCopyToSourceConversion() {
        val methodBuilder = bindingManager.copyToSourceMethodBuilder
        if (methodBuilder != null) {
            val accessorName = getSourceAccessor(false)
            if (accessorName != null) {
                var accessorMethodName = "source.get$methodNameSuffix()"
                val type = TypeName.get(field.asType())
                if (!type.isPrimitive) {
                    val fieldElement = bindingManager.getElement(type.toString())
                    val ccAnnotation = fieldElement.getAnnotation(CarbonCopy::class.java)
                    if (ccAnnotation != null) {
                        accessorMethodName = "convert(source.get$methodNameSuffix())"
                    }
                }
                methodBuilder.addStatement("copy.$accessorName($accessorMethodName)")
            } else {
                methodBuilder.addStatement("\$T.copyField(source, \"$name\", copy, \"$originalName\")", ClassName.get("carboncopy.annotations", "CarbonCopyUtil"))
            }
        }
    }


    /**
     * Returns the mapped field name
     */
    private fun getFieldName(field: Element) = field.getAnnotation(CarbonCopyRename::class.java)?.name ?: field.simpleName.toString()

    /**
     * Returns the mapped field name. Maps to CC type if the field type is @CarbonCopy type
     */
    private fun getFieldType(field: Element): TypeName {
        val type = TypeName.get(field.asType())
        return if (!type.isPrimitive) {
            val fieldElement = bindingManager.getElement(type.toString())
            val ccAnnotation = fieldElement.getAnnotation(CarbonCopy::class.java)
            if (ccAnnotation != null) {
                ClassName.get(MoreElements.getPackage(fieldElement).qualifiedName.toString(), ccAnnotation.name)
            } else {
                type
            }
        } else {
            type
        }
    }

    /**
     * Returns the get/set accessor method name for this field based on the parameter
     *
     * If [CarbonCopyAccessor] is specified, uses that.
     * If not, check whether the get/setFieldName method exist
     */
    private fun getSourceAccessor(getter: Boolean): String? {
        val ccAccessorAnnotation = field.getAnnotation(CarbonCopyAccessor::class.java)
        return if (ccAccessorAnnotation != null) {
            if (getter) ccAccessorAnnotation.getter else ccAccessorAnnotation.setter
        } else {
            var accessorName: String? = if (getter) "get$originalMethodNameSuffix" else "set$originalMethodNameSuffix"
            if (bindingManager.classElement.enclosedElements.none { it is ExecutableElement && it.simpleName.toString() == accessorName }) {
                accessorName = null
            }
            accessorName
        }
    }

}
