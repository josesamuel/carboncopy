package carboncopy.sample;

import junit.framework.Assert;

import org.junit.Test;

import carboncopy.CarbonCopyConverter;

public class CopyTest {

    @Test
    public void testSourceToCopy() {
        SampleClass sampleClass = new SampleClass();
        sampleClass.setIntData(1);
        sampleClass.setString("Hello");
        sampleClass.setBaseData(3);

        SampleClass2 sampleClass2 = new SampleClass2();
        sampleClass2.setSecondSampleData(2);
        sampleClass.setSampleClass2(sampleClass2);

        SampleClassPOJO sampleClassPOJO = CarbonCopyConverter.convert(sampleClass);

        Assert.assertEquals(sampleClass.getIntData(), sampleClassPOJO.getIntData());
        Assert.assertEquals(sampleClass.getString(), sampleClassPOJO.getStringData());
        Assert.assertEquals(sampleClass.getBaseData(), sampleClassPOJO.getBaseData3());

        SecondSampleCopy secondSampleCopy = sampleClassPOJO.getSampleClass2();
        Assert.assertEquals(sampleClass2.getSecondSampleData(), secondSampleCopy.getSecondSample());
    }

    @Test
    public void testCopyToSource() {
        SampleClassPOJO copyClass = new SampleClassPOJO();
        copyClass.setIntData(1);
        copyClass.setStringData("Hello");
        copyClass.setBaseData3(5);

        SecondSampleCopy secondSampleCopy = new SecondSampleCopy();
        secondSampleCopy.setSecondSample(2);
        copyClass.setSampleClass2(secondSampleCopy);

        SampleClass source = CarbonCopyConverter.convert(copyClass);

        Assert.assertEquals(copyClass.getIntData(), source.getIntData());
        Assert.assertEquals(copyClass.getStringData(), source.getString());
        Assert.assertEquals(copyClass.getBaseData3(), source.getBaseData());

        SampleClass2 secondSource = source.getSampleClass2();
        Assert.assertEquals(secondSource.getSecondSampleData(), secondSampleCopy.getSecondSample());
    }

}
