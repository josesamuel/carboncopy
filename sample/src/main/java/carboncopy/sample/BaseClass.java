package carboncopy.sample;

import carboncopy.annotations.CarbonCopyAccessor;
import carboncopy.annotations.CarbonCopyExclude;
import carboncopy.annotations.CarbonCopyRename;

/**
 * Parent of a class that gets carbon copied.
 * <p>
 * Data from parent class can also get copied/renamed as specified
 */
public class BaseClass {


    @CarbonCopyExclude
    private int someData;


    @CarbonCopyRename(name = "baseData3")
    private int baseData;

    //Non standard getter and setter
    @CarbonCopyAccessor(getter = "getBString", setter = "setBString")
    private String baseString;

    private float[] floatArray;

    public int getBaseData() {
        return baseData;
    }

    public void setBaseData(int baseData) {
        this.baseData = baseData;
    }

    public String getBString() {
        return baseString;
    }

    public void setBString(String baseString) {
        this.baseString = baseString;
    }

    public void setFloatArray(float[] floatArrray) {
        this.floatArray = floatArrray;
    }

    public float[] getFloatArray() {
        return floatArray;
    }
}
