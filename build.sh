#!/bin/bash
rm *.class *.jar
javac DataValue.java
javac ForecastDataValue.java
javac MapUpdateTimer.java
javac TimeUpdateTimer.java
javac UpdateUITimer.java
javac UpdateSensorTimer.java
javac PiWeather.java
#jar cmvf MANIFEST.MF PiWeather.jar *.class *.MF
