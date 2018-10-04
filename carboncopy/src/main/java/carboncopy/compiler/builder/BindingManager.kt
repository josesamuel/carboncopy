package carboncopy.compiler.builder


import carboncopy.annotations.CarbonCopy
import com.google.auto.common.MoreElements
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic


/**
 * Holds the [ProcessingEnvironment] and starting point of the copy process
 *
 * @author js
 */
class BindingManager(private val processingEnvironment: ProcessingEnvironment, val classElement: Element, private val converterClassBuilder: TypeSpec.Builder) {

    internal val packageName = MoreElements.getPackage(classElement).qualifiedName.toString()
    internal val originalClassName = classElement.simpleName.toString()
    internal val ignoredFields = classElement.getAnnotation(CarbonCopy::class.java).ignoredFields
    internal val generateSetters = classElement.getAnnotation(CarbonCopy::class.java).generateSetters
    private val copyNameGiven: String? = classElement.getAnnotation(CarbonCopy::class.java).name
    internal val copyClassName = if (copyNameGiven.isNullOrEmpty()) originalClassName + "POJO" else copyNameGiven!!
    internal val filer = processingEnvironment.filer
    private val elementUtils = processingEnvironment.elementUtils
    internal var sourceToCopyMethodBuilder: MethodSpec.Builder? = null
    internal var copyToSourceMethodBuilder: MethodSpec.Builder? = null
    private val CONVERTER_METHOD_NAME = "convert"
    private val CONVERTER_METHOD_PARAMETER_NAME = "source"
    internal var constructorBuilder = if (!generateSetters) MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC) else null


    /**
     * Called to start the carbon copy creation
     */
    fun generateCarbonCopy() {
        if (canCreateSourceInstance()) {
            copyToSourceMethodBuilder = createConverterMethod(ClassName.get(classElement.asType()), ClassName.get(packageName, copyClassName), CONVERTER_METHOD_PARAMETER_NAME, false)
        } else {
            processingEnvironment.messager.printMessage(Diagnostic.Kind.WARNING, "No default constructor found for ${classElement.simpleName}")
        }
        sourceToCopyMethodBuilder = createConverterMethod(ClassName.get(packageName, copyClassName), ClassName.get(classElement.asType()), CONVERTER_METHOD_PARAMETER_NAME, !generateSetters)
        val carbonCopyBuilder = CarbonCopyClassBuilder(this)
        carbonCopyBuilder.build()

        if (copyToSourceMethodBuilder != null) {
            converterClassBuilder.addMethod(copyToSourceMethodBuilder!!.addStatement("return copy").build())
        }
        converterClassBuilder.addMethod(sourceToCopyMethodBuilder!!.addStatement("return copy").build())
    }

    /**
     * Create the covert method for this class
     */
    private fun createConverterMethod(returnType: TypeName, parameterType: TypeName, parameterName: String, constructorBuilder: Boolean): MethodSpec.Builder {
        var methodBuilder = MethodSpec.methodBuilder(CONVERTER_METHOD_NAME)
                .returns(returnType)
                .addParameter(parameterType, parameterName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addJavadoc("Converts {@link \$T} -> {@link \$T}\n", parameterType, returnType)

        methodBuilder.beginControlFlow("if ($parameterName == null)")
        methodBuilder.addStatement("return null")
        methodBuilder.endControlFlow()

        methodBuilder = if (!constructorBuilder) {
            methodBuilder.addStatement("\$T copy = new $returnType()", returnType)
        } else {
            methodBuilder.addCode("\$T copy = new $returnType(", returnType)
        }
        return methodBuilder
    }


    /**
     * Returns a [Element] for the given class
     */
    fun getElement(className: String): Element? {
        var filteredClassName = className
        val templateStart = className.indexOf('<')
        if (templateStart != -1) {
            filteredClassName = className.substring(0, templateStart).trim { it <= ' ' }
        }
        return elementUtils.getTypeElement(filteredClassName)
    }

    private fun canCreateSourceInstance() =
            classElement.enclosedElements.any {
                it.simpleName.toString() == "<init>"
                        && it is ExecutableElement
                        && it.modifiers.contains(Modifier.PUBLIC)
                        && it.parameters.isEmpty()
            }

    fun fail(string: String) {
        processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, string);
    }


}
