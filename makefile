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
	BME280_Sensor.java \
	BMP280_Sensor.java \
	DHT11_Sensor.java \
	DHT22_Sensor.java \
	DataValue.java \
	Dummy_Sensor.java \
	ForecastDataValue.java \
	MapUpdateTimer.java \
	SensorUpdateTimer.java \
	TimeUpdateTimer.java \
	TrendData.java \
	TrendDisplayPanel.java \
	UIUpdateTimer.java \
	WxSensor.java \
	AirportData.java

default: classes

all: jar docs

jar: classes 
	jar cfm PiWeather.jar MANIFEST.MF *.class icons sensors

test_jar: jar
	cp PiWeather.jar ~/tmp

install: jar
	cp PiWeather.jar ~/Documents/Soar-High_Website/soar-high.com/charlie/

classes: $(CLASSES:.java=.class)

docs: classes
	$(JD) $(JDFLAGS) $(CLASSES)

clean:
	$(RM) *.class
	$(RM) *.ctxt
	$(RM) *.jar
	$(RM) -rf $(DOCDIR)/*
