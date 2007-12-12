
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
 * WeatherApp.fx
 *
 * Created on Sep 28, 2007, 4:12:30 PM
 */

package weatherfx;

import java.lang.System;
import java.lang.Thread;
import javafx.ui.*;
import javafx.ui.canvas.*;
import weatherfx.service.YahooWeatherService as YWS;

/**
 * Main class of the app
 * 
 * @author breh
 */

var weather1 = Weather{};
var weather2 = Weather{};
var weather3 = Weather{};

//var canvasViewport = CanvasViewport{currentWidth: 50 currentHeight: 50};

var f = Frame {
    background: black
    content: Box {
        orientation: VERTICAL:Orientation
        content: [
            //Canvas { content: weather1 viewport: canvasViewport scaleToFit: true},
            Canvas { content: weather1},
            Canvas { content: weather2},
            Canvas { content: weather3}
        ]
    }
    title: "WeatherFX"
    onClose: operation() { System.exit(0); }
    resizable: false
    centerOnScreen:true
    visible:true    
};


operation showWeather(weatherCode:String, weather:Weather) {
    do later {
        var yws:YWS;     
        do {
            yws = new YWS(weatherCode, false);
            do later {
                var wm = WeatherModel{};
                wm.loadFromYWS(yws);
                weather.weatherModel = wm;
            }
        }
    }
}


// show the weather information
showWeather("EZXX0012",weather1);
showWeather("FRXX0076",weather2);
showWeather("94303",weather3);

return f;
