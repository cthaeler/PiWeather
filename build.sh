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
javac WxSensor.java
javac BME280_Sensor.java
javac BMP280_Sensor.java
javac DHT11_Sensor.java
javac DHT22_Sensor.java
javac Dummy_Sensor.java
javac PiWeather.java
jar cfm PiWeather.jar MANIFEST.MF *.class
