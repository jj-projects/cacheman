# cacheman		

## Description

This java application changes Groundworks cache gpx files as follows:
 * adds comment field including Terrain, Difficulty and alike to get this information into an etrex Vista


####Description:

  * Sample for cmt element:
   * <cmt>TC-CU-D1.5-T1</cmt>

 * TC = Traditional Cache
 * MC = Multi-Cache
 * VC = Virtual Cache


 * CU = Container Unknown
 * CM = Container Micro
 * CS = Container Small
 * CR = Container Regular
 * CL = Container Large

 * D<num> = Difficulty <Zahl>
 
 * T<num> = Terrain <Zahl>



## Ant Build

On project level execute:
	ant -buildfile build/build.xml run
	
## Usage

java -classpath cacheman.jar de.jjprojects.cacheman.cacheman

## Author

Jörg Jünger ( jjprojects, joerg@jj-projects.de ), JJ-Projects Jörg Jünger

