
#Usage: PiWeather -s [DHT11, DHT22, BME280, DUMMY] -td 4 -f
#  -s         Sensor type, one of DHT11, DHT22, BME280 or DUMMY The DUMMY sensor is a simulatored sensor for testing
#  -f         Full frame
#  -td <num>  Show num (1-30) days of trend data.  0 == cycle through # of days
#  -ftd       Generate Fake Trend Data and exit
#  -wx        Save Wx files for later analysis
#  -l         Specify a location
#  -ll        Show a list of known locations and exit
#  -airport   Use an airport (must be US) as a location
#  -v         Verbose

java -Xdock:icon=icon.gif PiWeather -debug 5 -td 20 -s HTML http://10.0.0.101/html/WxPi/sensor.html
