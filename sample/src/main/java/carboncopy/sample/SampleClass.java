package carboncopy.sample;

import carboncopy.annotations.CarbonCopy;
import carboncopy.annotations.CarbonCopyAccessor;

/**
 * A sample class whose carbon copy gets generated
 */
@CarbonCopy
public class SampleClass extends BaseClass {


    private int intData = 0;

    //Non standard getter and setter
    @CarbonCopyAccessor(getter = "getString", setter = "setString")
    private String stringData;

    //a field that itself is again carbon copied
    private SampleClass2 sampleClass2;


    public int getIntData() {
        return intData;
    }

    public void setIntData(int intData) {
        this.intData = intData;
    }

    public String getString() {
        return stringData;
    }

    public void setString(String stringData) {
        this.stringData = stringData;
    }


    public SampleClass2 getSampleClass2() {
        return sampleClass2;
    }

    public void setSampleClass2(SampleClass2 sampleClass2) {
        this.sampleClass2 = sampleClass2;
    }
}
