package carboncopy.annotations;


import java.lang.reflect.Field;

/**
 * Utility method used for conversion
 */
public class CarbonCopyUtil {

    /**
     * Copy a value of source field to destination field.
     * Used if a public getter/setter of source object is not found
     */
    public static void copyField(Object sourceObject, String sourceFieldName, Object destinationObject, String destinationFieldName) {
        if (sourceObject != null && destinationObject != null) {
            Field sourcesField = getField(sourceObject.getClass(), sourceFieldName);
            Field destinationField = getField(destinationObject.getClass(), destinationFieldName);
            if (sourcesField != null && destinationField != null) {
                try {
                    sourcesField.setAccessible(true);
                    destinationField.setAccessible(true);
                    destinationField.set(destinationObject, sourcesField.get(sourceObject));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Finds the given fiend from the given class or its parent classes if any
     */
    private static Field getField(Class sourceClass, String sourceFieldName) {
        try {
            return sourceClass != null ? sourceClass.getDeclaredField(sourceFieldName) : null;
        } catch (NoSuchFieldException e) {
            return getField(sourceClass.getSuperclass(), sourceFieldName);
        }
    }

}
