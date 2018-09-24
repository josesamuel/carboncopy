package carboncopy.sample;

import carboncopy.annotations.CarbonCopy;

/**
 * A sample class whose carbon copy gets generated
 * <p>
 * Name can be specified for the copy. If didnt specified, the copy would have been SampleClass2POJO
 */
@CarbonCopy(name = "SecondSampleCopy", ignoredFields = {"ignoredField"})
public class SampleClass2 {

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
