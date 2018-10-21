
/**
 * Get data from the Airport Data base.  If the file isn't found in
 * the cache try to get it from the web
 *
 * @author Charles Thaeler
 * @version 1.0
 */

import java.nio.file.*;
import java.io.*;
import java.net.*;
import java.net.URL;

public class AirportData
{
    /** the airport.dat file */
    private static final String msAirportDataFilename = "data/airports.dat";

    /**
     * Constructor for objects of class AirportData
     */
    public AirportData()
    {
    }

    /**
     * FindAirport()
     * Find airport data from airports.dat.  The airports.dat file is from OpenFlights.org
     *    URL: https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat
     * 
     * @param ICAO_Name 4-letter ICAO
     * 
     * @return the airport data string (null if not found)
     * 
     */
    public static String FindAirport(String ICAO_Name)
    {
        // file format, comma seperated list
        // Airport ID     Unique OpenFlights identifier for this airport.
        // Name           Name of airport. May or may not contain the City name.
        // City           Main city served by airport. May be spelled differently from Name.
        // Country        Country or territory where airport is located. See countries.dat to cross-reference to ISO 3166-1 codes.
        // IATA           3-letter IATA code. Null if not assigned/unknown.
        // ICAO           4-letter ICAO code.
        //                   Null if not assigned.
        // Latitude       Decimal degrees, usually to six significant digits. Negative is South, positive is North.
        // Longitude      Decimal degrees, usually to six significant digits. Negative is West, positive is East.
        // Altitude       In feet.
        // Timezone       Hours offset from UTC. Fractional hours are expressed as decimals, eg. India is 5.5.
        // DST            Daylight savings time. One of 
        //                     E (Europe),
        //                     A (US/Canada),
        //                     S (South America),
        //                     O (Australia),
        //                     Z (New Zealand),
        //                     N (None)
        //                     U (Unknown). See also: Help: Time
        // Tz database time zone   Timezone in "tz" (Olson) format, eg. "America/Los_Angeles".
        // Type           Type of the airport. Value "airport" for air terminals, "station" for train stations, "port" for ferry terminals and "unknown" if not known. In airports.csv, only type=airport is included.
        // Source         Source of this data. "OurAirports" for data sourced from OurAirports, "Legacy" for old data not matched to OurAirports (mostly DAFIF), "User" for unverified user contributions. In airports.csv, only source=OurAirports is included.
    
        // Sample KDVO
        // 8138,"Marin County Airport - Gnoss Field","Novato","United States",\N,"KDVO",38.143600463867,-122.55599975586,2,-8,"A","America/Los_Angeles","airport","OurAirports"

        File file = new File(msAirportDataFilename);
        
        if (!file.exists()) {
            // try to download the file
            try {
                File dataDir = new File("data");
                // if the directory does not exist, create it
                if (!dataDir.exists()) {
                    dataDir.mkdir();
                }
                InputStream in = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat").openStream();
                Files.copy(in, Paths.get(msAirportDataFilename));
            } catch (IOException x) {
                System.err.println(x);
            }
        }

        if (file.exists() && file.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;  
                while ((line = reader.readLine()) != null)
                {
                    String name = GetAirportICAOName(line);
                    if (name != null && name.toUpperCase().equals(ICAO_Name.toUpperCase())) {
                        reader.close();
                        return line;
                    }
                }
                reader.close();
            } catch (Exception e) {
                System.err.format("Exception occurred trying to read '%s'.", msAirportDataFilename);
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * GetAirportICAOName() Get the ICAO airport name from an airport data string
     * 
     * @param airportdata airport data string
     * 
     * @return airport ICAO name (or null)
     */
    public static String GetAirportICAOName(String airportdata)
    {
        // Sample KDVO
        // 8138,"Marin County Airport - Gnoss Field","Novato","United States",\N,"KDVO",38.143600463867,-122.55599975586,2,-8,"A","America/Los_Angeles","airport","OurAirports"
        String[] data = airportdata.split(",");
        if (data.length > 7) { // make sure we have lat and long
            String ICAO_Name = data[5].replaceAll("\"", "");
            return ICAO_Name;
        }
        return null;
    }
    
    /**
     * GetAirportCountry() Get the country from an airport data string
     * 
     * @param airportdata airport data string
     * 
     * @return Country string
     */
    public static String GetAirportCountry(String airportdata)
    {
        // Sample KDVO
        // 8138,"Marin County Airport - Gnoss Field","Novato","United States",\N,"KDVO",38.143600463867,-122.55599975586,2,-8,"A","America/Los_Angeles","airport","OurAirports"
        String[] data = airportdata.split(",");
        if (data.length > 7) { // make sure we have lat and long
            String country = data[3].replaceAll("\"", "");
            return country;
        }
        return null;
    }
    
    /**
     * GetAirportLatitude()  Get an airport's Latitude
     * 
     * @param airport   the airport data string
     * 
     * @return the latitude of the airport (-999 if not found)
     */
    public static double GetAirportLatitude(String airport)
    {
        String[] data = airport.split(",");
        if (data.length > 7) { // make sure we have lat and long
            return Double.parseDouble(data[6]);
        }
        return -999;
    }
    
    /**
     * GetAirportLongitude()  Get an airport's Longitude
     * 
     * @param airport   the airport data string
     * 
     * @return the longitude of the airport (-999 if not found)
     */
    public static double GetAirportLongitude(String airport)
    {
        String[] data = airport.split(",");
        if (data.length > 7) { // make sure we have lat and long
            return Double.parseDouble(data[7]);
        }
        return -999;
    }


}
