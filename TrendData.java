import java.io.Serializable;
import java.io.*;

import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Write a description of class TrendData here.
 * 
 * @author Charles Thaeler
 * @version 15 Feb 2017
 */
public class TrendData
{
    private class TData
    {
        /** time of the data was captures */
        private LocalDateTime mDateTime;
        /** observation time from the web */
        private String mObsTime;
        /** temperature from the web */
        private double mTemp;
        /** humidity from the web */
        private double mHumidity;
        /** barometric pressure from the web */
        private double mBarometer;
        /** temperature from the sensor */
        private double mSensorTemp;
        /** humidity from the sensor */
        private double mSensorHumidity;
        /** barometric pressure from the sensor */
        private double mSensorBarometer;
        
    
        /**
         * Constructor for objects of class TrendData
         */
        public TData()
        {
            // initialise instance variables
            mDateTime = LocalDateTime.now();
            mTemp = 0.0;
            mHumidity = 0.0;
            mBarometer = 0.0;
            mObsTime = "";
            mSensorTemp = 0.0;
            mSensorHumidity = 0.0;
            mSensorBarometer = 0.0;
        }
        
        TData(TData td)
        {
            mDateTime = td.mDateTime;
            mTemp = td.mTemp;
            mHumidity = td.mHumidity;
            mBarometer = td.mBarometer;
            mObsTime = td.mObsTime;
            mSensorTemp = td.mSensorTemp;
            mSensorHumidity = td.mSensorHumidity;
            mSensorBarometer = mSensorBarometer;
        }
        
        /**
         * Constructor that takes data
         * 
         * @param time              time we collected the data
         * @param obstime           time of the observation from the web
         * @param temp              temperature of the observation from the web
         * @param humidity          humidity of the observation from the web
         * @param barometer         barometer of the observation from the web
         * @param sensorTemp        temperature from the sensor
         * @param sensorHumidity    humidity from the sensor
         * @param sensorBarometer   barometer from the sensor
         */
        TData(LocalDateTime time, String obstime, double temp, double humidity, double barometer,
                        double sensorTemp, double sensorHumidity, double sensorBarometer)
        {
            mDateTime = time;
            mObsTime = obstime;
            mTemp = temp;
            mHumidity = humidity;
            mBarometer = barometer;
            mSensorTemp = sensorTemp;
            mSensorHumidity = sensorHumidity;
            mSensorBarometer = sensorBarometer;
        }
        
        /**
         * GetDatTime()  get the time of the data entry
         * 
         * @return date time info
         */
        public LocalDateTime GetDateTime()
        {
            return mDateTime;
        }
        
        /**
         * SetDateTime()  Set the time the data was collected
         * 
         * @param time to save
         *  
         */
        public void SetDateTime(LocalDateTime time)
        {
            mDateTime = time;
        }
        
        /**
         * GetTemp()  Get the temperature
         * 
         * @return temperature
         */
        public double GetTemp()
        {
            return mTemp;
        }
        
        /**
         * SetTemp()  Set the temperature
         * 
         * @param temp the temperature
         * 
         */
        public void SetTemp(double temp)
        {
            mTemp = temp;
        }
        
        /**
         * GetHumidity() Get the humdity
         * 
         * @return humidity
         */
        public double GetHumidity()
        {
            return mHumidity;
        }
        
        /**
         * SetHumidity()  Set the humidity
         * 
         * @param humidity the humidity
         * 
         */
        public void SetHumidity(double humidity)
        {
            mHumidity = humidity;
        }
        
        /**
         * GetBarameter() get the barometric pressure
         * 
         * @return barometer
         */
        public double GetBarometer()
        {
            return mBarometer;
        }
        
        /**
         * SetBarometer() set the barometric presssure
         * 
         * @param barometer  the barometric pressure
         * 
         */
        public void SetBarometer(double barometer)
        {
            mBarometer = barometer;
        }
    
            
        /**
         * GetObsTime()  Get the time of the observation from the web
         * 
         * @return obs time as a string
         */
        public String GetObsTime()
        {
            return mObsTime;
        }
        
        /**
         * SetObsTime()  Set the observation time
         * 
         * @param obstime  the observation time
         * 
         */
        public void SetObsTime(String obstime)
        {
            mObsTime = obstime;
        }
        
        
        /**
         * GetSensorTemp()  Get the sensor temperature
         * 
         * @return sensor temperature
         */
        public double GetSensorTemp()
        {
            return mSensorTemp;
        }
        
        /**
         * SetSensorTemp()  set the sensor temperature
         * 
         * @param temp  the sensor temperature
         * 
         */
        public void SetSensorTemp(double temp)
        {
            mSensorTemp = temp;
        }
        
        /**
         * GetSensorHumidity() return the sensor humidity
         * 
         * @return sensor humidity
         * 
         */
        public double GetSensorHumidity()
        {
            return mSensorHumidity;
        }
        
        /**
         * SetSensorHumidity() set the sensor humidity value
         * 
         * @param humidity  the sensor humidity
         * 
         */
        public void SetSensorHumidity(double humidity)
        {
            mSensorHumidity = humidity;
        }
        
        /**
         * GetSensorBarometer()  Get the sensor barometric pressure
         * 
         * @return sensor barometer
         *
         */
        public double GetSensorBarometer()
        {
            return mSensorBarometer;
        }
    
        
        /**
         * SetSensorBarometer() set the sensor barometer
         *
         * @param barometer sensor barometer value
         *
         */
        public void SetSensorBarometer(double barometer)
        {
            mSensorBarometer = barometer;
        }
    
        
        /**
         * toString() conver the data to a string
         * 
         * @return the data string
         */
        public String toString()
        {
            return "TData[dt=" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(mDateTime) +
                              ", obs=" + mObsTime +
                              ", temp=" + String.format("%3.0f", mTemp) + "/" + String.format("%3.0f", mSensorTemp) +
                              ", hum=" + String.format("%3.0f", mHumidity) + "/" + String.format("%3.0f", mSensorHumidity) +
                              ", pres=" + String.format("%5.2f", mBarometer) + "/" + String.format("%5.2f", mSensorBarometer) +
                              "]";
                
        }
    }
    
    
    /** the trend data */
    private ArrayList<TData> mTrendData;
    
    TrendData()
    {
        mTrendData = new ArrayList<TData>();
    }
    
    TrendData(TrendData copy)
    {
        mTrendData = new ArrayList<TData>();
        for (TData td : copy.mTrendData) {
            TData ntd = new TData(td);
            mTrendData.add(ntd);
        }
    }
    
    
    public int NumValues()
    {
        return mTrendData.size();
    }
    
    
    /**
     * Constructor that takes data
     * 
     * @param time              time we collected the data
     * @param obstime           time of the observation from the web
     * @param temp              temperature of the observation from the web
     * @param humidity          humidity of the observation from the web
     * @param barometer         barometer of the observation from the web
     * @param sensorTemp        temperature from the sensor
     * @param sensorHumidity    humidity from the sensor
     * @param sensorBarometer   barometer from the sensor
     */
    public void AddValueSet(LocalDateTime time, String obstime, double temp, double humidity, double barometer,
                    double sensorTemp, double sensorHumidity, double sensorBarometer)
    {
        TData td = new TData(time, obstime, temp, humidity, barometer, sensorTemp, sensorHumidity, sensorBarometer);
        mTrendData.add(td);
    }


    public void DeleteByIndex(int idx)
    {
        mTrendData.remove(idx);
    }
    
    
    /**
     * GetDatTime()  get the time of the data entry
     * 
     * @return date time info
     */
    public LocalDateTime GetDateTime(int idx)
    {
        return mTrendData.get(idx).GetDateTime();
    }
    

    
    /**
     * GetTemp()  Get the temperature
     * 
     * @return temperature
     */
    public double GetTemp(int idx)
    {
        return mTrendData.get(idx).GetTemp();
    }
    

    
    /**
     * GetHumidity() Get the humdity
     * 
     * @return humidity
     */
    public double GetHumidity(int idx)
    {
        return mTrendData.get(idx).GetHumidity();
    }
    
 
    
    /**
     * GetBarameter() get the barometric pressure
     * 
     * @return barometer
     */
    public double GetBarometer(int idx)
    {
        return mTrendData.get(idx).GetBarometer();
    }
    


        
    /**
     * GetObsTime()  Get the time of the observation from the web
     * 
     * @return obs time as a string
     */
    public String GetObsTime(int idx)
    {
        return mTrendData.get(idx).GetObsTime();
    }
    
    
    
    /**
     * GetSensorTemp()  Get the sensor temperature
     * 
     * @return sensor temperature
     */
    public double GetSensorTemp(int idx)
    {
        return mTrendData.get(idx).GetSensorTemp();
    }
    
    
    /**
     * GetSensorHumidity() return the sensor humidity
     * 
     * @return sensor humidity
     * 
     */
    public double GetSensorHumidity(int idx)
    {
        return mTrendData.get(idx).GetSensorHumidity();
    }
    
    /**
     * GetSensorBarometer()  Get the sensor barometric pressure
     * 
     * @return sensor barometer
     *
     */
    public double GetSensorBarometer(int idx)
    {
        return mTrendData.get(idx).GetSensorBarometer();
    }


    /**
     * toString() conver the data to a string
     * 
     * @return the data string
     */
    public String toString()
    {
        String str = "TrendData ----";
        for (TData td : mTrendData) {
            str += " " + td + "\n";
        }
        return str;
            
    }
    







    /**
     * SetDataFromString() set a TData from a saved string
     * 
     * @param version   the version of the data file
     * @param s         the string of data "|" deliniated
     * @param td        TendData to fill in
     * 
     */
    private void SetDataFromString(int version, String s, TData td)
    {
        String[] data = s.split("\\|");
        switch(version) {
        case 1:
            td.SetDateTime(LocalDateTime.parse(data[0]));
            td.SetObsTime(data[1]);
            td.SetTemp(Double.parseDouble(data[2]));
            td.SetHumidity(Double.parseDouble(data[3]));
            td.SetBarometer(Double.parseDouble(data[4]));
            td.SetSensorTemp(Double.parseDouble(data[5]));
            td.SetSensorHumidity(Double.parseDouble(data[6]));
            break;
        case 2:
            td.SetDateTime(LocalDateTime.parse(data[0]));
            td.SetObsTime(data[1]);
            td.SetTemp(Double.parseDouble(data[2]));
            td.SetHumidity(Double.parseDouble(data[3]));
            td.SetBarometer(Double.parseDouble(data[4]));
            td.SetSensorTemp(Double.parseDouble(data[5]));
            td.SetSensorHumidity(Double.parseDouble(data[6]));
            td.SetSensorBarometer(Double.parseDouble(data[7]));
            break;
        }
    }
    
    
    /**
     * ReadFromFile() Read Trend Data in text format
     * 
     */
    public void ReadFromFile(String filename)
    {
        File f = new File(filename);
        if (f.exists() && f.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                int version = 1;
                if ((line = reader.readLine()) != null) {
                    // the first line is the version number
                    version = Integer.parseInt(line);
                }
    
                while ((line = reader.readLine()) != null)
                {  
                    TData td = new TData();
                    SetDataFromString(version, line, td);
                    try {
                        mTrendData.add(td);
                    } catch (Exception e) {
                        PiWeather.DumpError("TreadData:ReadFromFile: Bad add to ArrayList", e);
                    }
                }
                reader.close();
            } catch (Exception e) {
                PiWeather.DumpError("TreadData:ReadFromFile: Error occurred trying to read " + filename, e);
            }
        }
    }
    
    public void WriteToFile(String filename)
    {
        try {
            File file = new File(filename);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file);
            writer.println("2"); //version number
            for (TData td : mTrendData) {
                String s =  td.GetDateTime().toString() + "|" +
                            td.GetObsTime() + "|" +
                            String.format("%f", td.GetTemp()) + "|" +
                            String.format("%f", td.GetHumidity()) + "|" +
                            String.format("%f", td.GetBarometer()) + "|" +
                            String.format("%f", td.GetSensorTemp()) + "|" +
                            String.format("%f", td.GetSensorHumidity()) + "|" +
                            String.format("%f", td.GetSensorBarometer());
                writer.println(s);
            }
            writer.close();
        } catch (IOException ioe) {
            PiWeather.DumpError("TrendData:WriteToFile: error writing trend data file", ioe);
        }
    }





    /**
     * DumpTrendData(String header)  Dump the trend data to standard out.
     * 
     * @param header a header to show first
     * 
     */
    public void DumpTrendData(String header, String footer)
    {
        System.out.println(header);
        int i = 0;
        for (TData td : mTrendData) {
            System.out.print(String.format("%4d ", i++));
            System.out.println(td.toString());
        }

        if (mTrendData.size() > 1) {
            System.out.println("First: " + mTrendData.get(0).GetDateTime());
            System.out.println("Last:  " + mTrendData.get(mTrendData.size()-1).GetDateTime());
        }
        System.out.println(footer);
    }
    
    
    
    
    /**
     * CleanTrendData()  Clean up trend data.  If it's unreasonable grab the previous entry to "fix" it
     * 
     */
    public void CleanTrendData()
    {
        // look for humidity zeros and ...  First one better be good...
        for (int i = 1; i < mTrendData.size(); i++)
        {
            if (mTrendData.get(i).GetBarometer() < 25 || mTrendData.get(i).GetBarometer() > 32)
                mTrendData.get(i).SetBarometer(mTrendData.get(i-1).GetBarometer());
            if (mTrendData.get(i).GetHumidity() < 0 || mTrendData.get(i).GetHumidity() > 100)
                mTrendData.get(i).SetHumidity(mTrendData.get(i-1).GetHumidity());
            if (mTrendData.get(i).GetSensorHumidity() < 0 || mTrendData.get(i).GetSensorHumidity() > 100)
                mTrendData.get(i).SetSensorHumidity(mTrendData.get(i-1).GetSensorHumidity());
            if (mTrendData.get(i).GetSensorTemp() < -40 || mTrendData.get(i).GetSensorTemp() > 150)
                mTrendData.get(i).SetSensorTemp(mTrendData.get(i-1).GetSensorTemp());
        }
    }
    
    
    
    
    /**
     * GenFakeTrendData()  generate 3 days of fake data for trouble shooting
     * 
     */
    public void GenFakeTrendData()
    {
        // Generate 3 days worth
        for (int i = 24*6*3; i > 0; i--) {
            LocalDateTime n = LocalDateTime.now();
            LocalDateTime t = n.minusMinutes(i*10);
            // 2017-02-07T07:55:00
            String obstime = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(t) +"T" + DateTimeFormatter.ofPattern("HH:mm:ss").format(t);
            // -20 to 120
            double temp = -20 + i % 140;
            // 0 to 100
            double humidity = i % 100;
            // 27 to 32
            double press = 27 + (i%500)/100.0;
            mTrendData.add(new TData(t, obstime, temp, humidity, press, temp+3, humidity+3, press + 0.2));
        }
    }
}
