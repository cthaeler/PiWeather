import board
import digitalio
import busio
import time
import adafruit_bme280

# Create library object using our Bus I2C port
i2c = busio.I2C(board.SCL, board.SDA)
bme280 = adafruit_bme280.Adafruit_BME280_I2C(i2c)

#print("\nTemperature: %0.1f C" % bme280.temperature)
#print("Humidity: %0.1f %%" % bme280.humidity)
#print("Pressure: %0.1f hPa" % bme280.pressure)

temp_f = bme280.temperature * 9 / 5 + 32;
humidity = bme280.humidity
pressure = bme280.pressure * 0.02953;

print("{0:0.1f}|{1:0.2f}|{2:0.2f}".format(temp_f, humidity, pressure))
