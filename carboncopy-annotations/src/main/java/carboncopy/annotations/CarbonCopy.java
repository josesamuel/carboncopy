package carboncopy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotate on a class to create a Carbon Copy of it.
 * The Carbon Copy class is a POJO representation of the annotated class
 * <p>
 * If a name is specified, a class with that name will be created,
 * otherwise a class with a name POJO appended to this class will be created
 * <p>
 */
@Retention(CLASS)
@Target(TYPE)
public @interface CarbonCopy {

    /**
     * Specifies the name for the CarbonCopy class to be created.
     * By default this is the class name appended with POJO
     */
    String name() default "";

    /**
     * Array of fields to be ignored if any
     * This can also be specified using {@link CarbonCopyAccessor}
     */
    String[] ignoredFields() default {};

    /**
     * Whether to add setter method to the generated copy.
     * Default true.
     * If set to false, instead of empty default constructor, one that accepts all the fields will be generated
     */
    boolean generateSetters() default true;
}
