package carboncopy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotate on a field to specify its getter and setter to be used while converting to/from the copy.
 * If not specified, the converter looks for standard get/setFieldName method and uses it if found.
 * If not found, it uses reflection to do the conversion.
 *
 * @see CarbonCopy
 */
@Retention(CLASS)
@Target(FIELD)
public @interface CarbonCopyAccessor {

    /**
     * Specifies the getter method name for this field
     */
    String getter();

    /**
     * Specifies the setter method name for this field
     */
    String setter();
}
