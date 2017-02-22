from Adafruit_BMP280 import *

sensor = BMP280(mode=BMP280_OSAMPLE_8)

degrees = sensor.read_temperature()
pascals = sensor.read_pressure()
hectopascals = pascals / 100

#print 'Timestamp = {0:0.3f}'.format(sensor.t_fine)
#print 'Temp      = {0:0.3f} deg C'.format(degrees)
#print 'Pressure  = {0:0.2f} hPa'.format(hectopascals)
#print 'Pressure  = {0:0.2f} Pa'.format(pascals)

temp = degrees * 9 / 5 + 32;
pressure = pascals * 0.0002953;

print ('{0:0.1f}|{2:0.2f}').format(temp, pressure)
