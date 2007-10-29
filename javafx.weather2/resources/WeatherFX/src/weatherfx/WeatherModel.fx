
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
 * WeatherModel.fx
 *
 * Created on Oct 1, 2007, 12:40:12 PM
 */

package weatherfx;

import weatherfx.service.YahooWeatherService as YWS;
import java.lang.System;

/**
 *  Holds model data for the weather widget
 * 
 * @author breh
 */


UNKONWN:Integer = 0;
CLEAR:Integer = 1;
CLOUDS:Integer = 2;
RAIN:Integer = 3;
SNOW:Integer = 4;
THUNDER:Integer = 5;
MOON:Integer = 6;


public class WeatherModel {

    public operation loadFromYWS(yws:YWS);
    public function windInformation(): String;

    public attribute cityName:String;
    public attribute temperature:Number;    
    public attribute windSpeed:Number;
    public attribute windDirection:Number;    

    public attribute todayLows:Number;
    public attribute todayHighs:Number;   
    public attribute todayConditionCode: Integer;

    public attribute tomorrowLows:Number;
    public attribute tomorrowHighs:Number;    
    public attribute tomorrowConditionCode: Integer;

    private function translateConditionCode(conditionCode:Integer):Integer;
    
}


operation WeatherModel.translateDirectionToString(dir:Number):String {
    var windDirs = ["N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"];
    dir = dir % 360;    
    var i = ((dir + 22.5 ) / 45).intValue();
    return windDirs[i];
}

function WeatherModel.windInformation() {
     return translateDirectionToString(windDirection).concat(windSpeed.toString());
}


function WeatherModel.translateConditionCode(code:Integer) {
    return 
        if ((code >= 0) and ( code < 5))  then
            THUNDER:Integer
        else if ((code >= 5) and (code < 9)) then
            SNOW:Integer
        else if ((code >= 9) and (code < 13)) then
            RAIN:Integer
        else if ((code >= 13) and (code < 19)) then
            SNOW:Integer            
        else if ((code >= 19) and (code < 26)) then
            CLEAR:Integer                        
        else if ((code >= 26) and (code < 31)) then
            CLOUDS:Integer
        else if (code == 31) then
            MOON:Integer
        else if (code == 32) then
            CLEAR:Integer
        else if (code == 33) then
            MOON:Integer            
        else if (code == 34) then
            CLEAR:Integer            
        else if (code == 35) then
            RAIN:Integer              
        else if (code == 36) then
            CLEAR:Integer
        else if ((code >= 37) and (code < 40)) then
            THUNDER:Integer
        else if ((code >= 41) and (code < 44)) then
            SNOW:Integer
        else if (code == 44) then 
            CLOUDS:Integer
        else if (code == 45) then 
            THUNDER:Integer
        else if (code == 46) then 
            SNOW:Integer
        else if (code == 47) then 
            THUNDER:Integer            
        else UNKNOWN:Integer
    ;
}

operation WeatherModel.loadFromYWS(yws:YWS) {
    cityName = yws.getCityName();
    temperature = yws.getTemp();

    windSpeed = yws.getWindSpeed();
    windDirection = yws.getWindDirection(); 
    todayConditionCode = translateConditionCode(yws.getConditionCode());
    todayLows = yws.getLowsTemp(0);
    todayHighs = yws.getHighsTemp(0);

    tomorrowConditionCode = translateConditionCode(yws.getConditionCode(1));    
    tomorrowLows = yws.getLowsTemp(1);
    tomorrowHighs = yws.getHighsTemp(1);
    //System.out.println("conditions: {cityName} {yws.getConditionCode()} {yws.getConditionCode(1)} {todayConditionCode}");

}