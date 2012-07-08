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

Joerg Juenger ( jj-projects, joerg@jj-projects.de ), JJ-Projects Joerg Juenger

## Licenses

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
