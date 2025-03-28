# MON12

The MON12 application is designed to provide shift takers with basic 
informatiom on CLAS12 raw data, such as occupancies, ADC and TDC spectra, to 
check the detector functionality. Check the wiki for information on how to 
build, run or modify the application.
![](https://github.com/JeffersonLab/clas12mon/blob/master/images/Screen%20Shot%202021-06-09%20at%2019.18.04.png)


## Using different version of coatjava for mon12:

https://github.com/JeffersonLab/mon12/pull/66#issuecomment-2674250331

`bin/use_local_coatjava` does this:
```bash
coat_version=$(sed -n -e '/<dependencies>/,/<\/dependencies>/p' pom.xml | tr -d '\n\t ' \
| xmllint --xpath \
"//dependencies/dependency[groupId/text()='org.jlab.coat']/version/text()" - )

cp $CLAS12DIR/lib/clas/coat-libs-*-SNAPSHOT.jar \
  ~/.m2/repository/org/jlab/coat/coat-libs/${coat_version}/coat-libs-${coat_version}.jar
```


## Detector Data Grouping

And other undocumented and incomplete features.

### DetectorMonitor.getDataGroup()

This returns the object `IndexedList<DataGroup>(3)`.
  
[`IndexedList<>`](https://github.com/JeffersonLab/coatjava/blob/development/common-tools/clas-utils/src/main/java/org/jlab/utils/groups/IndexedList.java)
is in `coatjava/coat-libs` and is a hash map of N indices.
The 3 indices are used to build the hash for each entry.
**These indices are used.**

### DataGroup

A class from the `groot` library which is a wrapper on 3  LinkedHashMaps
with redundant/duplicate lookup maps. It has a constructor `DataGroup(int rows, 
int cols)` and methods to support getting row/col value, however, **the rows 
and cols are not used in any way**. They provide no functionality beyond their 
existence and the map uses an order index to look up contained objects.

Most of the time objects added to the `DataGroup` are looked by name string!

`DataGroup`s most useful feature is the ability to look up objects with methods 
which explicitly cast to specific types -- `H1F` and `H2F` is 99% of usage 
here.
here) .

