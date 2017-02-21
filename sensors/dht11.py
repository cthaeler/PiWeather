#!/usr/bin/python

import Adafruit_DHT

sensor = Adafruit_DHT.DHT11

pin = 4

# Try to grab a sensor reading.  Use the read_retry method which will retry up
# to 15 times to get a sensor reading (waiting 2 seconds between each retry).
humidity, temp = Adafruit_DHT.read_retry(sensor, pin)

temp = temp * 9 / 5 + 32;

if humidity is not None and temp is not None:
    print('{0:0.1f}|{1:0.1f}'.format(temp, humidity))
else:
    print('ERROR')
