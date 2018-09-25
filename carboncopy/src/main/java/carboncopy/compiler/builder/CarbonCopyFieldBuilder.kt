package carboncopy.compiler.builder

import carboncopy.annotations.CarbonCopy
import carboncopy.annotations.CarbonCopyAccessor
import carboncopy.annotations.CarbonCopyRename
import com.google.auto.common.MoreElements
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * Adds a field and its gettter and setter to the [TypeSpec.Builder] class builder
 */
class CarbonCopyFieldBuilder(private val bindingManager: BindingManager, private val classBuilder: TypeSpec.Builder, private val field: Element, private val fieldCount: Int) {

    private var genericTypeFound = false
    private val genericPairs = mutableListOf<Pair<TypeMirror, TypeName>>()
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
        if (bindingManager.generateSetters) {
            addSetter(classBuilder, name, type)
            addSourceToCopyConversion()
        } else {
            bindingManager.constructorBuilder
                    ?.addParameter(type, name)
                    ?.addStatement("this.$name = $name")

            addSourceToCopyConstructorConversion()
        }
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
                val fieldType = TypeName.get(field.asType())
                if (!fieldType.isPrimitive) {
                    val fieldElement = bindingManager.getElement(fieldType.toString())
                    val ccAnnotation = fieldElement?.getAnnotation(CarbonCopy::class.java)
                    if (ccAnnotation != null) {
                        accessorMethodName = "convert(source.$accessorName())"
                    }
                }
                if (genericTypeFound && type is ParameterizedTypeName) {
                    val collectionGenericType = getGenericCollectionImplementationClass(type)
                    if (collectionGenericType != null) {
                        val mapType = genericPairs.size == 2
                        methodBuilder.beginControlFlow("if ($accessorMethodName != null)")
                        methodBuilder.addStatement("\$T ${field.simpleName} = new \$T()", collectionGenericType.first, ClassName.get("java.util", collectionGenericType.second.toString()))
                        methodBuilder.beginControlFlow("for(\$T s:$accessorMethodName${if (mapType) ".keySet()" else ""})", genericPairs[0].first)
                        if (!mapType) {
                            //Collection type
                            methodBuilder.addStatement("${field.simpleName}.add(convert(s))")
                        } else {
                            //Map type
                            methodBuilder.addStatement("${field.simpleName}.put((\$T)convert((\$T)s), (\$T)convert((\$T)$accessorMethodName.get(s)))",
                                    genericPairs[0].second, genericPairs[0].first,
                                    genericPairs[1].second, genericPairs[1].first)
                        }
                        methodBuilder.endControlFlow()
                        methodBuilder.addStatement("copy.set$methodNameSuffix(${field.simpleName})")
                        methodBuilder.endControlFlow()
                    }
                } else {
                    methodBuilder.addStatement("copy.set$methodNameSuffix($accessorMethodName)")
                }
            } else {
                methodBuilder.addStatement("\$T.copyField(source, \"$originalName\", copy, \"$name\")", ClassName.get("carboncopy.annotations", "CarbonCopyUtil"))
            }
        }
    }

    /**
     * Add the statements for the source -> copy converter for this field
     */
    private fun addSourceToCopyConstructorConversion() {
        val methodBuilder = bindingManager.sourceToCopyMethodBuilder
        if (methodBuilder != null) {
            val accessorName = getSourceAccessor(true)
            if (accessorName != null) {
                var accessorMethodName = "source.$accessorName()"
                val fieldType = TypeName.get(field.asType())
                if (!fieldType.isPrimitive) {
                    val fieldElement = bindingManager.getElement(fieldType.toString())
                    val ccAnnotation = fieldElement?.getAnnotation(CarbonCopy::class.java)
                    if (ccAnnotation != null) {
                        accessorMethodName = "convert(source.$accessorName())"
                    }
                }
                if (fieldCount > 0) {
                    methodBuilder.addCode(", ")
                }
                methodBuilder.addCode("$accessorMethodName")

            } else {
                bindingManager.fail("NO getter found for $name")
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
                    val ccAnnotation = fieldElement?.getAnnotation(CarbonCopy::class.java)
                    if (ccAnnotation != null) {
                        accessorMethodName = "convert(source.get$methodNameSuffix())"
                    }
                }

                if (genericTypeFound && type is ParameterizedTypeName) {
                    val collectionGenericType = getGenericCollectionImplementationClass(type)
                    if (collectionGenericType != null) {
                        val mapType = genericPairs.size == 2
                        methodBuilder.beginControlFlow("if ($accessorMethodName != null)")
                        methodBuilder.addStatement("\$T ${field.simpleName} = new \$T()", collectionGenericType.first, ClassName.get("java.util", collectionGenericType.second.toString()))
                        methodBuilder.beginControlFlow("for(\$T s:$accessorMethodName${if (mapType) ".keySet()" else ""})", genericPairs[0].second)
                        if (!mapType) {
                            //Collection type
                            methodBuilder.addStatement("${field.simpleName}.add(convert(s))")
                        } else {
                            //Map type
                            methodBuilder.addStatement("${field.simpleName}.put((\$T)convert((\$T)s), (\$T)convert((\$T)$accessorMethodName.get(s)))",
                                    genericPairs[0].first, genericPairs[0].second,
                                    genericPairs[1].first, genericPairs[1].second)
                        }
                        methodBuilder.endControlFlow()
                        methodBuilder.addStatement("copy.$accessorName(${field.simpleName})")
                        methodBuilder.endControlFlow()
                    }
                } else {
                    methodBuilder.addStatement("copy.$accessorName($accessorMethodName)")
                }
            } else {
                methodBuilder.addStatement("\$T.copyField(source, \"$name\", copy, \"$originalName\")", ClassName.get("carboncopy.annotations", "CarbonCopyUtil"))
            }
        }
    }

    private fun getGenericCollectionImplementationClass(sourceType: ParameterizedTypeName): Pair<ParameterizedTypeName?, StringBuffer>? {
        val genericPair = Pair(ParameterizedTypeName.get(
                sourceType.rawType,
                *sourceType.typeArguments.toTypedArray()),
                StringBuffer())
        return when (sourceType.rawType.simpleName()) {
            "List", "ArrayList" -> {
                genericPair.second.append("ArrayList")
                return genericPair
            }
            "LinkedList" -> {
                genericPair.second.append("LinkedList")
                return genericPair
            }
            "Vector" -> {
                genericPair.second.append("Vector")
                return genericPair
            }
            "Stack" -> {
                genericPair.second.append("Stack")
                return genericPair
            }
            "Queue" -> {
                genericPair.second.append("Queue")
                return genericPair
            }

            "Set", "HashSet" -> {
                genericPair.second.append("HashSet")
                return genericPair
            }
            "TreeSet" -> {
                genericPair.second.append("TreeSet")
                return genericPair
            }

            "Map", "HashMap" -> {
                genericPair.second.append("HashMap")
                return genericPair
            }
            "Hashtable" -> {
                genericPair.second.append("Hashtable")
                return genericPair
            }
            else -> null
        }
    }


    /**
     * Returns the mapped field name
     */
    private fun getFieldName(field: Element) = field.getAnnotation(CarbonCopyRename::class.java)?.name
            ?: field.simpleName.toString()

    /**
     * Returns the mapped field name. Maps to CC type if the field type is @CarbonCopy type
     */
    private fun getFieldType(field: Element): TypeName {
        val typeMirror = field.asType()
        val type = TypeName.get(typeMirror)
        return if (!type.isPrimitive) {
            getMappedType(typeMirror) ?: if (typeMirror is DeclaredType) {
                typeMirror.typeArguments.forEach {
                    val mappedTypeName: TypeName? = getMappedType(it)
                    if (mappedTypeName != null) {
                        genericTypeFound = true
                    }
                    genericPairs.add(Pair(it, mappedTypeName ?: TypeName.get(it)))
                }

                if (genericTypeFound) {
                    return ParameterizedTypeName.get(
                            ClassName.get(typeMirror.asElement() as TypeElement),
                            *genericPairs.map { it.second }.toTypedArray())
                }
                type
            } else {
                type
            }
        } else {
            type
        }
    }

    private fun getMappedType(typeMirror: TypeMirror): TypeName? {
        val typeName = TypeName.get(typeMirror)
        val element = bindingManager.getElement(typeName.toString())
        val ccAnnotation = element?.getAnnotation(CarbonCopy::class.java)
        if (ccAnnotation != null) {
            val copyNameGiven: String? = ccAnnotation.name
            val copyClassName = if (copyNameGiven.isNullOrEmpty()) element.simpleName.toString() + "POJO" else copyNameGiven!!
            return ClassName.get(MoreElements.getPackage(element).qualifiedName.toString(), copyClassName)
        }

        return null
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
            getSourceAccessor(getter, bindingManager.classElement as TypeElement)
        }
    }

    /**
     * Recursively find the accessor method if it exist
     */
    private fun getSourceAccessor(getter: Boolean, element: TypeElement): String? {
        val accessorName: String? = if (getter) "get$originalMethodNameSuffix" else "set$originalMethodNameSuffix"
        if (element.enclosedElements.none { it is ExecutableElement && it.simpleName.toString() == accessorName }) {
            return if (element.superclass.kind != TypeKind.NONE) {
                getSourceAccessor(getter, bindingManager.getElement(ClassName.get(element.superclass).toString()) as TypeElement)
            } else {
                null
            }
        }
        return accessorName
    }

}
