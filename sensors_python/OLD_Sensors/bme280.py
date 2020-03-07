from Adafruit_BME280 import *

sensor = BME280(mode=BME280_OSAMPLE_8)

degrees = sensor.read_temperature()
pascals = sensor.read_pressure()
hectopascals = pascals / 100
humidity = sensor.read_humidity()

#print 'Timestamp = {0:0.3f}'.format(sensor.t_fine)
#print 'Temp      = {0:0.3f} deg C'.format(degrees)
#print 'Pressure  = {0:0.2f} hPa'.format(hectopascals)
#print 'Pressure  = {0:0.2f} Pa'.format(pascals)
#print 'Humidity  = {0:0.2f} %'.format(humidity)

temp = degrees * 9 / 5 + 32;
pressure = pascals * 0.0002953;

print ('{0:0.1f}|{1:0.2f}|{2:0.2f}').format(temp, humidity, pressure)
