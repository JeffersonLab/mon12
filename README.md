# MON12

The MON12 application is designed to provide shift takers with basic 
informatiom on CLAS12 raw data, such as occupancies, ADC and TDC spectra, to 
check the detector functionality. Check the wiki for information on how to 
build, run or modify the application.
![](https://github.com/JeffersonLab/clas12mon/blob/master/images/Screen%20Shot%202021-06-09%20at%2019.18.04.png)


## Using different version of coatjava for mon12:

https://github.com/JeffersonLab/mon12/pull/66#issuecomment-2674250331

`bin/use_local_coatjava` does mostly this:
```bash
coat_version=$(sed -n -e '/<dependencies>/,/<\/dependencies>/p' pom.xml | tr -d '\n\t ' \
| xmllint --xpath \
"//dependencies/dependency[groupId/text()='org.jlab.coat']/version/text()" - )

cp $CLAS12DIR/lib/clas/coat-libs-*-SNAPSHOT.jar \
  ~/.m2/repository/org/jlab/coat/coat-libs/${coat_version}/coat-libs-${coat_version}.jar
```



