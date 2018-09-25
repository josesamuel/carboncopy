package carboncopy.sample;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

    private List<SampleClass2> listOfSampleClass;
    private HashSet<SampleClass2> setOfSampleClass;
    private Map<String, SampleClass2> mapOfStringToSampleClass2;
    private Hashtable<SampleClass, SampleClass2> hashTableOfSampleClassToSampleClass2;



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

    public List<SampleClass2> getListOfSampleClass() {
        return listOfSampleClass;
    }

    public void setListOfSampleClass(List<SampleClass2> listOfSampleClass) {
        this.listOfSampleClass = listOfSampleClass;
    }

    public HashSet<SampleClass2> getSetOfSampleClass() {
        return setOfSampleClass;
    }

    public void setSetOfSampleClass(HashSet<SampleClass2> setOfSampleClass) {
        this.setOfSampleClass = setOfSampleClass;
    }

    public Map<String, SampleClass2> getMapOfStringToSampleClass2() {
        return mapOfStringToSampleClass2;
    }

    public void setMapOfStringToSampleClass2(Map<String, SampleClass2> mapOfStringToSampleClass2) {
        this.mapOfStringToSampleClass2 = mapOfStringToSampleClass2;
    }

    public Hashtable<SampleClass, SampleClass2> getHashTableOfSampleClassToSampleClass2() {
        return hashTableOfSampleClassToSampleClass2;
    }

    public void setHashTableOfSampleClassToSampleClass2(Hashtable<SampleClass, SampleClass2> hashTableOfSampleClassToSampleClass2) {
        this.hashTableOfSampleClassToSampleClass2 = hashTableOfSampleClassToSampleClass2;
    }
}
