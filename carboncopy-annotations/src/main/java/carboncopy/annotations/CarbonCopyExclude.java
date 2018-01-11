package carboncopy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotate on a field to be excluded while creating the carbon copy class
 *
 * @see CarbonCopy
 */
@Retention(CLASS)
@Target(FIELD)
public @interface CarbonCopyExclude {
}
