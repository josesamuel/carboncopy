package carboncopy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotate on a field to give a different name to this field on the carbon copied class
 *
 * @see CarbonCopy
 */
@Retention(CLASS)
@Target(FIELD)
public @interface CarbonCopyRename {

    /**
     * Specifies the name for this field
     */
    String name();
}
