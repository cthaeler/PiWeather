# Based on the follow by: Charles Thaeler
#
# Copyright (c) 2014 Adafruit Industries
# Author: Tony DiCola
#
# Based on the BMP280 driver with BME280 changes provided by
# David J Taylor, Edinburgh (www.satsignal.eu)
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
import logging
import time


# BMP280 default address.
BMP280_I2CADDR = 0x77

# Operating Modes
BMP280_OSAMPLE_1 = 1
BMP280_OSAMPLE_2 = 2
BMP280_OSAMPLE_4 = 3
BMP280_OSAMPLE_8 = 4
BMP280_OSAMPLE_16 = 5

# BMP280 Registers

BMP280_REGISTER_DIG_T1 = 0x88  # Trimming parameter registers
BMP280_REGISTER_DIG_T2 = 0x8A
BMP280_REGISTER_DIG_T3 = 0x8C

BMP280_REGISTER_DIG_P1 = 0x8E
BMP280_REGISTER_DIG_P2 = 0x90
BMP280_REGISTER_DIG_P3 = 0x92
BMP280_REGISTER_DIG_P4 = 0x94
BMP280_REGISTER_DIG_P5 = 0x96
BMP280_REGISTER_DIG_P6 = 0x98
BMP280_REGISTER_DIG_P7 = 0x9A
BMP280_REGISTER_DIG_P8 = 0x9C
BMP280_REGISTER_DIG_P9 = 0x9E

BMP280_REGISTER_CHIPID = 0xD0
BMP280_REGISTER_VERSION = 0xD1
BMP280_REGISTER_SOFTRESET = 0xE0

BMP280_REGISTER_CONTROL_HUM = 0xF2
BMP280_REGISTER_CONTROL = 0xF4
BMP280_REGISTER_CONFIG = 0xF5
BMP280_REGISTER_PRESSURE_DATA = 0xF7
BMP280_REGISTER_TEMP_DATA = 0xFA


class BMP280(object):
    def __init__(self, mode=BMP280_OSAMPLE_1, address=BMP280_I2CADDR, i2c=None,
                 **kwargs):
        self._logger = logging.getLogger('Adafruit_BMP.BMP085')
        # Check that mode is valid.
        if mode not in [BMP280_OSAMPLE_1, BMP280_OSAMPLE_2, BMP280_OSAMPLE_4,
                        BMP280_OSAMPLE_8, BMP280_OSAMPLE_16]:
            raise ValueError(
                'Unexpected mode value {0}.  Set mode to one of BMP280_ULTRALOWPOWER, BMP280_STANDARD, BMP280_HIGHRES, or BMP280_ULTRAHIGHRES'.format(mode))
        self._mode = mode
        # Create I2C device.
        if i2c is None:
            import Adafruit_GPIO.I2C as I2C
            i2c = I2C
        self._device = i2c.get_i2c_device(address, **kwargs)
        # Load calibration values.
        self._load_calibration()
        self._device.write8(BMP280_REGISTER_CONTROL, 0x3F)
        self.t_fine = 0.0

    def _load_calibration(self):

        self.dig_T1 = self._device.readU16LE(BMP280_REGISTER_DIG_T1)
        self.dig_T2 = self._device.readS16LE(BMP280_REGISTER_DIG_T2)
        self.dig_T3 = self._device.readS16LE(BMP280_REGISTER_DIG_T3)

        self.dig_P1 = self._device.readU16LE(BMP280_REGISTER_DIG_P1)
        self.dig_P2 = self._device.readS16LE(BMP280_REGISTER_DIG_P2)
        self.dig_P3 = self._device.readS16LE(BMP280_REGISTER_DIG_P3)
        self.dig_P4 = self._device.readS16LE(BMP280_REGISTER_DIG_P4)
        self.dig_P5 = self._device.readS16LE(BMP280_REGISTER_DIG_P5)
        self.dig_P6 = self._device.readS16LE(BMP280_REGISTER_DIG_P6)
        self.dig_P7 = self._device.readS16LE(BMP280_REGISTER_DIG_P7)
        self.dig_P8 = self._device.readS16LE(BMP280_REGISTER_DIG_P8)
        self.dig_P9 = self._device.readS16LE(BMP280_REGISTER_DIG_P9)


    def read_raw_temp(self):
        """Reads the raw (uncompensated) temperature from the sensor."""
        meas = self._mode
        self._device.write8(BMP280_REGISTER_CONTROL_HUM, meas)
        meas = self._mode << 5 | self._mode << 2 | 1
        self._device.write8(BMP280_REGISTER_CONTROL, meas)
        sleep_time = 0.00125 + 0.0023 * (1 << self._mode)
        sleep_time = sleep_time + 0.0023 * (1 << self._mode) + 0.000575
        sleep_time = sleep_time + 0.0023 * (1 << self._mode) + 0.000575
        time.sleep(sleep_time)  # Wait the required time
        msb = self._device.readU8(BMP280_REGISTER_TEMP_DATA)
        lsb = self._device.readU8(BMP280_REGISTER_TEMP_DATA + 1)
        xlsb = self._device.readU8(BMP280_REGISTER_TEMP_DATA + 2)
        raw = ((msb << 16) | (lsb << 8) | xlsb) >> 4
        return raw

    def read_raw_pressure(self):
        """Reads the raw (uncompensated) pressure level from the sensor."""
        """Assumes that the temperature has already been read """
        """i.e. that enough delay has been provided"""
        msb = self._device.readU8(BMP280_REGISTER_PRESSURE_DATA)
        lsb = self._device.readU8(BMP280_REGISTER_PRESSURE_DATA + 1)
        xlsb = self._device.readU8(BMP280_REGISTER_PRESSURE_DATA + 2)
        raw = ((msb << 16) | (lsb << 8) | xlsb) >> 4
        return raw


    def read_temperature(self):
        """Gets the compensated temperature in degrees celsius."""
        # float in Python is double precision
        UT = float(self.read_raw_temp())
        var1 = (UT / 16384.0 - self.dig_T1 / 1024.0) * float(self.dig_T2)
        var2 = ((UT / 131072.0 - self.dig_T1 / 8192.0) * (
        UT / 131072.0 - self.dig_T1 / 8192.0)) * float(self.dig_T3)
        self.t_fine = int(var1 + var2)
        temp = (var1 + var2) / 5120.0
        return temp

    def read_pressure(self):
        """Gets the compensated pressure in Pascals."""
        adc = self.read_raw_pressure()
        var1 = self.t_fine / 2.0 - 64000.0
        var2 = var1 * var1 * self.dig_P6 / 32768.0
        var2 = var2 + var1 * self.dig_P5 * 2.0
        var2 = var2 / 4.0 + self.dig_P4 * 65536.0
        var1 = (
               self.dig_P3 * var1 * var1 / 524288.0 + self.dig_P2 * var1) / 524288.0
        var1 = (1.0 + var1 / 32768.0) * self.dig_P1
        if var1 == 0:
            return 0
        p = 1048576.0 - adc
        p = ((p - var2 / 4096.0) * 6250.0) / var1
        var1 = self.dig_P9 * p * p / 2147483648.0
        var2 = p * self.dig_P8 / 32768.0
        p = p + (var1 + var2 + self.dig_P7) / 16.0
        return p

