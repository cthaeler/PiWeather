#
# makefile for PiWeather
#

DOCDIR=doc

JFLAGS = -g
JC = javac

JDFLAGS = -quiet -d $(DOCDIR) -private
JD = javadoc

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	PiWeather.java \
	WxSensor.java \
	SensorData.java \
	BME280_Sensor.java \
	BMP280_Sensor.java \
	DHT11_Sensor.java \
	DHT22_Sensor.java \
	DataValueUI.java \
	Dummy_Sensor.java \
	ForecastData.java \
	ForecastDataValueUI.java \
	MapUpdateTimer.java \
	SensorUpdateTimer.java \
	TimeUpdateTimer.java \
	TrendData.java \
	TrendDisplayPanel.java \
	UIUpdateTimer.java \
	WxWebDocUtils.java \
	AirportData.java

default: classes

all: jar docs

jar: classes 
	jar cfm PiWeather.jar MANIFEST.MF *.class icons sensors

test_jar: jar
	cp PiWeather.jar ~/tmp

install: jar
	cp PiWeather.jar ~/bin

classes: $(CLASSES:.java=.class)

docs: classes
	$(JD) $(JDFLAGS) $(CLASSES)

clean:
	$(RM) *.class
	$(RM) *.ctxt
	$(RM) *.jar
	$(RM) -rf $(DOCDIR)/*
