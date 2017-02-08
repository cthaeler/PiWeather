#!/bin/bash
rm *.class *.jar
javac DataValue.java
javac ForecastDataValue.java
javac TrendData.java
javac MapUpdateTimer.java
javac TimeUpdateTimer.java
javac UIUpdateTimer.java
javac SensorUpdateTimer.java
javac TrendDisplayPanel.java
javac PiWeather.java
#jar cmvf MANIFEST.MF PiWeather.jar *.class *.MF
