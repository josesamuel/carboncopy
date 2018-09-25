package carboncopy.sample;

import carboncopy.annotations.*;

/**
 * A sample class whose carbon copy gets generated
 * <p>
 * Name can be specified for the copy. If didnt specified, the copy would have been SampleClass2POJO
 */
@CarbonCopy(name = "SecondSampleCopy", ignoredFields = {"ignoredField"}, generateSetters = false)
public class SampleClass2 {


    @CarbonCopyAccessor(getter = "getSecondSampleData", setter = "setSecondSampleData")
    private int secondSample = 5;

    private int ignoredField = 5;
    int expectedField = 5;


    public void setSecondSampleData(int secondSampleData) {
        this.secondSample = secondSampleData;
    }


    public int getSecondSampleData() {
        return secondSample;
    }

    public int getExpectedField() {
        return expectedField;
    }

    public void setExpectedField(int expectedField) {
        this.expectedField = expectedField;
    }
}
