#!/bin/bash

echo "*building biosim"
echo "	-initializing biosim build...";
devRootDir=$BIOSIM_HOME
if [ -z "$devRootDir" ]
then
	devRootDir=".."
	echo "		-assuming BIOSIM_HOME is $devRootDir"
fi
JACORB_HOME="$devRootDir/jacorb"
genString="/generated"
genDir=$devRootDir$genString
if [ ! -e "$genDir" ]
then
	mkdir $genDir
	echo "		-creating generated directory"
fi
####################
#		SERVER INIT		#
####################
echo "	-building server"
echo "		-initializing server build"
serverGenString="/server"
serverGenDir=$genDir$serverGenString
if [ ! -e  "$serverGenDir" ]
then
	mkdir $serverGenDir
	echo "			-creating server directory"
fi
skeletonString="/skeletons"
skeletonDir=$serverGenDir$skeletonString
if [ ! -e  "$skeletonDir" ]
then
	mkdir $skeletonDir
	echo "			-creating skeletons directory"
fi
serverClassesString="/classes"
serverClassesDir=$serverGenDir$serverClassesString
if [ ! -e  "$serverClassesDir" ]
then
	mkdir $serverClassesDir
	echo "			-creating classes directory"
fi
relativeIDLDir="/biosim/idl/SIMULATION.idl"
fullIDLDir=$devRootDir$relativeIDLDir
echo "			-generating skeletons"
idlInvocation="java -classpath $JACORB_HOME/lib/idl.jar org.jacorb.idl.parser"
$idlInvocation  -nostub -d $skeletonDir $fullIDLDir
#######################
#		SERVER COMPILATION	#
#######################
echo "		-compiling server";
simString="SIMULATION"
simSkeletonDir="$skeletonDir/$simString"
serverDir="$devRootDir/biosim/server"
echo "			-compiling skeletons"
javac -d $serverClassesDir $simSkeletonDir/*.java
echo "			-compiling air"
javac -d $serverClassesDir -classpath $skeletonDir:$serverClassesDir:$CLASSPATH $serverDir/air/*.java
echo "			-compiling water"
javac -d $serverClassesDir -classpath $skeletonDir:$serverClassesDir:$CLASSPATH $serverDir/water/*.java
echo "			-compiling energy"
javac -d $serverClassesDir -classpath $skeletonDir:$serverClassesDir:$CLASSPATH $serverDir/energy/*.java
echo "			-compiling crew"
javac -d $serverClassesDir -classpath $skeletonDir:$serverClassesDir:$CLASSPATH $serverDir/crew/*.java
echo "			-compiling biomass"
javac -d $serverClassesDir -classpath $skeletonDir:$serverClassesDir:$CLASSPATH $serverDir/biomass/*.java
echo "*done building biosim"



