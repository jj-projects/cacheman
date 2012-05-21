# filelister		

## Description

This java application iterates through a directory structure starting at a given directory in the UI.
It provides special output for some details of mp3 files.

### Purpose:

It is ment to generate a list of mp3 files contained in a directory to keep the knowledge about titels being sampled from a audio CD into a folder.

## Ant Build

On project level execute:
	ant -buildfile build/build.xml -propertyfile build/build_FileLister.properties run
	
## Usage

java -classpath FileLister.jar de.jjprojects.filelister.FileLister

## Author

Jörg Jünger ( jjprojects, joerg@jj-projects.de ), JJ-Projects Jörg Jünger

