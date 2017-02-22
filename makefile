#
# makefile for PiWeather
#

JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	BME280_Sensor.java \
	BMP280_Sensor.java \
	DHT11_Sensor.java \
	DHT22_Sensor.java \
	DataValue.java \
	Dummy_Sensor.java \
	ForecastDataValue.java \
	MapUpdateTimer.java \
	PiWeather.java \
	SensorUpdateTimer.java \
	TimeUpdateTimer.java \
	TrendData.java \
	TrendDisplayPanel.java \
	UIUpdateTimer.java \
	WxSensor.java

default: classes

all: jar


jar: classes
	jar cfm PiWeather.jar MANIFEST.MF *.class icons sensors

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *.jar
