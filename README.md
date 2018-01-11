# CarbonCopy

Create POJO carbon copy of a class by simply adding **@CarbonCopy** annotation


```java
@CarbonCopy
public class MyDataClass {
    ...
}

```

This will generate a POJO carbon copy of the above class, with getters and setters of all the fields of the original class

```java
public class MyDataClassPOJO {
    ...
}

```
Easily convert between the source object and its copy using the auto generated **CarbonCopyConverter.convert** method

```java
MyDataClass myDataClass;

MyDataClassPOJO myDataPojo = CarbonCopyConverter.convert(myDataClass)

```



That's it! 


**Annotations**

* **@CarbonCopy** Add to a class to generates its POJO carbon copy. You could specify a custom name
* **@CarbonCopyExclude** Exclude fields that you don't want in the copy
* **@CarbonCopyRename** Rename a field in the copy
* **@CarbonCopyAccessor** Specify custom accessors of source class



Getting CarbonCopy
--------

Gradle dependency

```groovy
dependencies {
	//Replace "api" with "compile" for pre AndroidStudio 3
    api 'com.josesamuel:carboncopy-annotations:1.0.1'
    annotationProcessor 'com.josesamuel:carboncopy:1.0.1'
}
```


License
-------

    Copyright 2018 Joseph Samuel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


