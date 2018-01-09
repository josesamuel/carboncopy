package carboncopy.sample;

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
    private int baseData2;

    public int getBaseData() {
        return baseData2;
    }

    public void setBaseData(int baseData) {
        this.baseData2 = baseData;
    }
}
