/*
 * Copyright (c) 2007, Sun Microsystems, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems, Inc. nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
/*
 * YahooWeatherService.java
 * 
 * Created on Sep 29, 2007, 9:35:09 AM
 * 
 */

package weatherfx.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A connector to Yahoo Weather service. 
 *
 * @author breh
 */
public class YahooWeatherService {

    private static final String address = "http://weather.yahooapis.com/forecastrss?p=";
    private static final String addressC = "http://weather.yahooapis.com/forecastrss?u=c&p=";
    
    private static final String CITY_SF = "94102";    
    
    
    private String city;
    private String region;
    private String country;
            
    
    private int windSpeed;
    private int windDirection;
    
    private int conditionTemp;
    private String conditionString;
    private int conditionCode;
    private String conditionImageURL;
    
    private List<Forecast> forecast = new ArrayList<Forecast>();
    
    private static class Forecast {
        
        private String day;
        private int tempLows;
        private int tempHighs;
        
        private String conditionString;
        private int conditionCode;
        
    }
    
    
    
    public YahooWeatherService()  throws IOException  {
        this(CITY_SF, false);
    }
    
    public YahooWeatherService(String zipCode, boolean celsius) throws IOException {        
        InputStream is = getInputStreamFromURL(createWeatherServiceURL(zipCode, celsius));
        try {
            InputSource inputSource = new InputSource(is);
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new YWSParser());
            reader.parse(inputSource);
        } catch (SAXException saxe) {
            throw new IOException(saxe.getMessage());
        } finally {
            if (is != null) {
                is.close();
            }
        }
        
    }
    
    
    private static final InputStream getInputStreamFromURL(URL url) throws IOException {
        URLConnection con = url.openConnection();
        con.connect();
        return con.getInputStream();        
    }
    
    private static URL createWeatherServiceURL(String ZIP, boolean celsius) throws IOException {
        String addr = celsius ? addressC : address;
        URL url = new URL(addr+ZIP);
        return url;
    }

    
    public String getCityName() {
        return city;
    }
    
    public String getRegionName() {
        return region;
    }
    
    
    public int getTemp() {
        return conditionTemp;
    }
    
    public int getLowsTemp(int forecastDay) {
        if (forecast.size() > forecastDay) {
            return forecast.get(forecastDay).tempLows;
        } else {
            return 0;
        }
    }
    
    public int getHighsTemp(int forecastDay) {
        if (forecast.size() > forecastDay) {
            return forecast.get(forecastDay).tempHighs;
        } else {
            return 0;
        }        
    }
    
    
    public int getWindSpeed() {
        return windSpeed;
    }
    
    
    public int getWindDirection() {
        return windDirection;
    }
    
    public String getConditionURLString() {
        //System.out.println("URL: "+conditionImageURL);
        return conditionImageURL;
    }
    
    public int getConditionCode() {
        return conditionCode;
    }
    
    
    public int getConditionCode(int forecastDay) {
        if (forecast.size() > forecastDay) {
            return forecast.get(forecastDay).conditionCode;
        } else {
            return 0;
        } 
    }
    

    private static final String LOCATION_EL = "location";
    private static final String LOCATION_CITY = "city";
    private static final String LOCATION_REGION = "region";
    private static final String LOCATION_COUNTRY = "country";
    
    private static final String WIND_EL = "wind";
    private static final String WIND_SPEED = "speed";
    private static final String WIND_DIRECTION = "direction";
    
    private static final String CONDITION_EL = "condition";
    private static final String CONDITION_CODE = "code";
    private static final String CONDITION_TEMP = "temp";
    private static final String CONDITION_DESCRIPTION = "text";
    private static final String CONDITION_IMG_EL = "description";
    
    private static final String FORECAST_EL = "forecast";
    private static final String FOREACAST_DAY = "day";
    private static final String FOREACAST_LOW = "low";
    private static final String FOREACAST_HIGH = "high";
    private static final String FOREACAST_CODE = "code";
    private static final String FOREACAST_DESCRIPTION = "text";
    
    private class YWSParser extends DefaultHandler {

        
        private boolean inDescription = false;
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (LOCATION_EL.equals(localName)) {
                city = attributes.getValue(LOCATION_CITY);
                region = attributes.getValue(LOCATION_REGION);
                country = attributes.getValue(LOCATION_COUNTRY);                
            }
            if (CONDITION_EL.equals(localName)) {
                conditionTemp = Integer.parseInt(attributes.getValue(CONDITION_TEMP));                
                conditionCode = Integer.parseInt(attributes.getValue(CONDITION_CODE));
            }
            if (CONDITION_IMG_EL.equals(localName)) {
                inDescription = true;
            }
            if (WIND_EL.equals(localName)) {
                windDirection = Integer.parseInt(attributes.getValue(WIND_DIRECTION));
                windSpeed = Integer.parseInt(attributes.getValue(WIND_SPEED));
            }
            if (FORECAST_EL.equals(localName)) {
                Forecast f = new Forecast();
                f.day = attributes.getValue(FOREACAST_DAY);
                f.tempLows = Integer.parseInt(attributes.getValue(FOREACAST_LOW));
                f.tempHighs = Integer.parseInt(attributes.getValue(FOREACAST_HIGH));
                f.conditionCode = Integer.parseInt(attributes.getValue(FOREACAST_CODE));
                f.conditionString = attributes.getValue(FOREACAST_DESCRIPTION);
                forecast.add(f);
            }
            
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (CONDITION_IMG_EL.equals(localName)) {
                inDescription = false;
            }
        }
        
        
        

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inDescription) {
                // read the array to cdata
                String s = new String(ch, start, length);                
                //System.out.println("S = "+s);
                // get img/src 
                int imgLocation = s.indexOf("img");
                if (imgLocation < 0) return;
                           
                s = s.substring(imgLocation);               
                int srcLocation = s.indexOf("src");
                if (srcLocation < 0) return;
                
                s = s.substring(srcLocation);
                int urlLocationBegin = s.indexOf('"');
                if (urlLocationBegin < 0) return;
                
                s = s.substring(urlLocationBegin+1);                
                int urlLocationEnd = s.indexOf('"');
                if (urlLocationEnd < 0) return;
                
                conditionImageURL = s.substring(0, urlLocationEnd);
            }            
        }
    
        
    
    }
    
    

}
