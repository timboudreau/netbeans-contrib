
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
 * Weather.fx
 *
 * Created on Sep 20, 2007, 9:32:45 AM
 */

package weatherfx;

import javafx.ui.UIElement;
import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.filter.*;
import java.lang.System;
import java.lang.Thread;

/**
 * Class holding all the graphics data. Original imported from Adobe Illustrator
 * using SVG export and SVG -> JavaFX converter.
 * 
 * @author breh
 */


public class Weather extends CompositeNode {
    
    // gfx
    public function weatherGradient(): LinearGradient;    
    private function createTomorrowClickableArea();
    private function createTodayClickableArea();
    
    private attribute outlineShape:Shape;
    private attribute todayConditionImages:ImageView*;
    private attribute tomorrowConditionImages:ImageView*;

    private function getConditionImageForCode(conditionImages:ImageView*,code:Integer):ImageView;

    // business logic
    private attribute weatherModel: WeatherModel;
    private function windInformation(): String;

    // animation support 
    private attribute todayNodesOpacity: Number;
    private attribute tomorrowNodesOpacity: Number;
    
    private attribute lowsTriangleTransform:TransformHelper;
    private attribute highsTriangleTransform:TransformHelper;
    
    private attribute todayHighsTY:Number;
    private attribute todayLowsTY:Number;
    private attribute tomorrowHighsTY:Number;
    private attribute tomorrowLowsTY:Number;
    
    private attribute loadingScreenOpacity:Number;
    private attribute weatherScreenOpacity:Number;
}

attribute Weather.todayNodesOpacity = 1.0;
attribute Weather.tomorrowNodesOpacity = bind 1.0 - todayNodesOpacity;

attribute Weather.loadingScreenOpacity = 1.0;
attribute Weather.weatherScreenOpacity = 0.0;

attribute Weather.tomorrowLowsTY = 0;
attribute Weather.tomorrowHighsTY = 0;

attribute Weather.lowsTriangleTransform = TransformHelper{};
attribute Weather.highsTriangleTransform = TransformHelper{
        scale: bind lowsTriangleTransform.scale
};

function Weather.getConditionImageForCode(conditionImages:ImageView*,code:Integer):ImageView {
    return conditionImages[code];
}

attribute Weather.todayConditionImages = [
    ImageView{},
    ImageView{image: Image{url:"{__DIR__}/imgs/sun.png"}},
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/cloud"
        baseName: "cloud_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    }, 
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/rain"
        baseName: "rain_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    },
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/snow"
        baseName: "snow_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    }, 
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/lighning"
        baseName: "lighning_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 32
    }, 
    ImageView{image: Image{url:"{__DIR__}/imgs/moon.png"}},
];

attribute Weather.tomorrowConditionImages = [
    ImageView{},
    ImageView{image: Image{url:"{__DIR__}/imgs/sun.png"}},
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/cloud"
        baseName: "cloud_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    }, 
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/rain"
        baseName: "rain_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    },
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/snow"
        baseName: "snow_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 33
    }, 
    AnimatedImage {
        baseURL: "{__DIR__}/imgs/lighning"
        baseName: "lighning_00"
        extension: "png"
        frameDelay: 85
        imagesCount: 31
    }, 
    ImageView{image: Image{url:"{__DIR__}/imgs/moon.png"}},
];


trigger on Weather.weatherModel[oldValue] = newValue {
    loadingScreenOpacity = [1.0,0.99..0.0] dur 1000;
    weatherScreenOpacity = [0.0,0.01..1.0] dur 1000;
}


function Weather.createTodayClickableArea() {
    return Rect {
        var: self
        x:0 y:129 width: 90 height:12
        fill: white
        opacity: 0.01
        onMousePressed: operation (e:CanvasMouseEvent) {                       
            todayNodesOpacity = [0.0,0.01 .. 1.0] dur 1000;            
            
            lowsTriangleTransform.scale = [2.2,2.19 .. 1.0] dur 800 linear;

            lowsTriangleTransform.tx = [105..0] dur 1000;
            lowsTriangleTransform.ty = [-6..0] dur 1000;
            
            highsTriangleTransform.tx = [10..0] dur 1000;            
            highsTriangleTransform.ty = [11..0] dur 1000;         
            
            todayLowsTY = [70..0] dur 1000;
            todayHighsTY = [-70..0] dur 1000;
            tomorrowLowsTY = [0..70] dur 500;
            tomorrowHighsTY = [0..-70] dur 500;
        }

    };
}


function Weather.createTomorrowClickableArea() {
    return Rect {
        var: self
        x:90 y:129 width: 145 height:12 
        fill: white
        opacity: 0.01
        onMousePressed: operation (e:CanvasMouseEvent) {                       
            todayNodesOpacity = [1.0,0.99 .. 0.0] dur 1000;
            lowsTriangleTransform.scale = [1.0,1.01 .. 2.2] dur 800 linear;
            
            highsTriangleTransform.tx = [0..10] dur 1000;
            highsTriangleTransform.ty = [0..11] dur 1000;

            lowsTriangleTransform.tx = [0..105] dur 1000;                     
            lowsTriangleTransform.ty = [0..-6] dur 1000;                    
            
            todayLowsTY = [0..70] dur 500;
            todayHighsTY = [0..-70] dur 500;
            tomorrowLowsTY = [70..0] dur 1000;
            tomorrowHighsTY = [-70..0] dur 1000;
        }        
    };
}







function Weather.weatherGradient() = [
    LinearGradient {
        transform: [
            matrix(1.0, 0.0, 0.0, -1.0, 22.52, -15.1484),
        ]
        gradientUnits: USER_SPACE_ON_USE
        x1: 94.6499
        x2: 94.6499
        y1: -151.0098
        y2: -66.7269
        stops: [
            Stop {
                offset: 0.0
                color: rgba(0x10, 0x72, 0xBA, 0xff)
            },
            Stop {
                offset: 0.0835
                color: rgba(0x4F, 0x8C, 0xC5, 0xff)
            },
            Stop {
                offset: 0.1685
                color: rgba(0x70, 0xA5, 0xD1, 0xff)
            },
            Stop {
                offset: 0.254
                color: rgba(0x8B, 0xBB, 0xDA, 0xff)
            },
            Stop {
                offset: 0.3389
                color: rgba(0x9E, 0xCC, 0xE1, 0xff)
            },
            Stop {
                offset: 0.423
                color: rgba(0xAA, 0xD6, 0xE5, 0xff)
            },
            Stop {
                offset: 0.5055
                color: rgba(0xAF, 0xDB, 0xE7, 0xff)
            },
            Stop {
                offset: 0.5692
                color: rgba(0xA8, 0xD4, 0xE4, 0xff)
            },
            Stop {
                offset: 0.6617
                color: rgba(0x94, 0xC3, 0xDD, 0xff)
            },
            Stop {
                offset: 0.772
                color: rgba(0x76, 0xAA, 0xD3, 0xff)
            },
            Stop {
                offset: 0.8948
                color: rgba(0x4F, 0x8B, 0xC5, 0xff)
            },
            Stop {
                offset: 1.0
                color: rgba(0x10, 0x72, 0xBA, 0xff)
            },
        ]
    },
];


// outline shape used for clipping the top weather node
attribute Weather.outlineShape =  Path {
            transform: translate(10,-57)
            d: [
                MoveTo {
                    x: 233.84
                    y: 133.471
                    absolute: true
                },
                CurveTo {
                    x1: 0.0
                    y1: 3.907
                    x2: -1.516
                    y2: 7.076
                    x3: -3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 3.887
                    absolute: true
                },
                CurveTo {
                    x1: -1.871
                    y1: 0.0
                    x2: -3.387
                    y2: -3.169
                    x3: -3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: -67.76
                    absolute: false
                },
                CurveTo {
                    x1: 0.0
                    y1: -3.908
                    x2: 1.516
                    y2: -7.076
                    x3: 3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 226.57
                    absolute: false
                },
                CurveTo {
                    x1: 1.867
                    y1: 0.0
                    x2: 3.383
                    y2: 3.167
                    x3: 3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: 133.471
                    absolute: true
                },
                ClosePath {},
            ]
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
        };



function Weather.composeNode() = 
Group {
    //clip: Clip { shape: Rect {x:0 y:0 width: 245 height: 85}}    
    clip: Clip { shape: outlineShape}    
    content: [
        Group {
            transform: translate(10,1)
            opacity: bind loadingScreenOpacity
            content: [/*
                Rect {x:0 y:0 width:245 height:85 fill:black},
                Text {content: "Loading Weather Data ..." 
                    fill:white 
                    x:122 y:42
                    halign: CENTER:HorizontalAlignment
                    valign: MIDDLE:VerticalAlignment
                    font: Font {faceName: 'Arial', style: PLAIN, size: 14}
                }*/
                LoadingScreen {}
            ]
        },
Group { 
    var weekdaysFont = Font {faceName: 'Arial', style: BOLD, size: 8.6149}
    opacity: bind weatherScreenOpacity
    //opacity: 0
    transform: translate(10,-57)
    content:[
        Path {
            d: [
                MoveTo {
                    x: 233.84
                    y: 133.471
                    absolute: true
                },
                CurveTo {
                    x1: 0.0
                    y1: 3.907
                    x2: -1.516
                    y2: 7.076
                    x3: -3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 3.887
                    absolute: true
                },
                CurveTo {
                    x1: -1.871
                    y1: 0.0
                    x2: -3.387
                    y2: -3.169
                    x3: -3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: -67.76
                    absolute: false
                },
                CurveTo {
                    x1: 0.0
                    y1: -3.908
                    x2: 1.516
                    y2: -7.076
                    x3: 3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 226.57
                    absolute: false
                },
                CurveTo {
                    x1: 1.867
                    y1: 0.0
                    x2: 3.383
                    y2: 3.167
                    x3: 3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: 133.471
                    absolute: true
                },
                ClosePath {},
            ]
            fill: weatherGradient()
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
        },


        
        Text {
            verticalAlignment: BASELINE
            content: 'TODAY'
            font: weekdaysFont
            fill: rgba(0xF2, 0xF2, 0xF2, 0xff)
            transform: [
                matrix(1.0, 0.0, 0.0, 1.0, 6.6519, 137.2471),
            ]
            x: 0.0
            y: 0.0
        },        
        createTodayClickableArea(),

        Text {
            verticalAlignment: BASELINE
            content: 'TOMORROW'
            font: weekdaysFont
            fill: rgba(0xF2, 0xF2, 0xF2, 0xff)
            transform: [
                matrix(1.0, 0.0, 0.0, 1.0, 96.0806, 137.248),
            ]
            x: 0.0
            y: 0.0
        },
        createTomorrowClickableArea(), 
        // today condition
        Group { 
            transform: translate(0,58)
            content: bind getConditionImageForCode(todayConditionImages, weatherModel.todayConditionCode)
            opacity: bind todayNodesOpacity/2.0
        },
        // tomorrow condition
        Group { 
            transform: translate(0,58)
            content: bind getConditionImageForCode(tomorrowConditionImages, weatherModel.tomorrowConditionCode)
            opacity: bind tomorrowNodesOpacity/2.0
        },
        
        

        // wind information
        Group {
            opacity: bind todayNodesOpacity
            content: 
        Group {
            
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]
            content: [
        Group {
            transform: [Rotate { angle: bind weatherModel.windDirection - 68
                                 cx: 192 cy: 98} ] 
            content: [
        Path {
            d: [
                MoveTo {
                    x: 180.494
                    y: 85.268
                    absolute: true
                },
                LineTo {
                    x: 25.994
                    y: 3.425
                    absolute: false
                },
                LineTo {
                    x: -15.73
                    y: 22.77
                    absolute: false
                },
                LineTo {
                    x: 180.494
                    y: 85.268
                    absolute: true
                },
                ClosePath {},
            ]
            fill: black
        },
        Text {            
            verticalAlignment: BASELINE
            halign: CENTER
            content: bind weatherModel.windInformation()
            font: Font {faceName: 'Arial', style: PLAIN, size: 13.5526}
            transform: [
                //rotate (67,178,90)
                matrix(0.3866, 0.9865, -0.9074, 0.4203, 178.8594, 90.377),
            ]
        },
        ]
        }
        ]}
        },
        
        // outline
        Path {
            d: [
                MoveTo {
                    x: 233.84
                    y: 133.471
                    absolute: true
                },
                CurveTo {
                    x1: 0.0
                    y1: 3.907
                    x2: -1.516
                    y2: 7.076
                    x3: -3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 3.887
                    absolute: true
                },
                CurveTo {
                    x1: -1.871
                    y1: 0.0
                    x2: -3.387
                    y2: -3.169
                    x3: -3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: -67.76
                    absolute: false
                },
                CurveTo {
                    x1: 0.0
                    y1: -3.908
                    x2: 1.516
                    y2: -7.076
                    x3: 3.387
                    y3: -7.076
                    smooth: false
                    absolute: false
                },
                HLine {
                    x: 226.57
                    absolute: false
                },
                CurveTo {
                    x1: 1.867
                    y1: 0.0
                    x2: 3.383
                    y2: 3.167
                    x3: 3.383
                    y3: 7.076
                    smooth: false
                    absolute: false
                },
                VLine {
                    y: 133.471
                    absolute: true
                },
                ClosePath {},
            ]
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
        },

        // temperature
        Group {
            opacity: bind todayNodesOpacity
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]
            content: [
        Circle {
            cx: 148.475
            cy: 85.083
            radius: 2.45
            stroke: rgba(0xED, 0x22, 0x27, 0xff)
            strokeWidth: 2.0
        },
        Text {
            verticalAlignment: BASELINE
            content: bind weatherModel.temperature.toString()
            font: Font {faceName: 'Arial', style: BOLD, size: 48.6668}
            fill: rgba(0xED, 0x1E, 0x24, 0xff)
            transform: [
                matrix(1.0, 0.0, 0.0, 1.0, 88.8457, 116.3926),
            ]
            x: 0.0
            y: 0.0
        },]
        },


        Group {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
            content: 
        Group {
            transform: [translate(bind highsTriangleTransform.tx, bind highsTriangleTransform.ty),translate(37,93), scale(bind highsTriangleTransform.scale, bind highsTriangleTransform.scale), translate(-37,-93)]
            content: [
        // highs triangle
        Path {
            
            d: [
                MoveTo {
                    x: 27.552
                    y: 98.673
                    absolute: true
                },
                LineTo {
                    x: 8.012
                    y: -13.257
                    absolute: false
                },
                LineTo {
                    x: 8.021
                    y: 13.257
                    absolute: false
                },
                HLine {
                    x: 27.552
                    absolute: true
                },
                ClosePath {},
            ]
            fill: black
        }
        ]}},
        // lows triangle
        Group {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
            content:        
        Group {
            transform: [translate(bind lowsTriangleTransform.tx, bind lowsTriangleTransform.ty),translate(37,109), scale(bind lowsTriangleTransform.scale, bind lowsTriangleTransform.scale), translate(-37,-109)]            
            content: 
        Path {                
            d: [
                MoveTo {
                    x: 43.922
                    y: 101.64
                    absolute: true
                },
                LineTo {
                    x: -8.056
                    y: 13.942
                    absolute: false
                },
                LineTo {
                    x: -8.045
                    y: -13.942
                    absolute: false
                },
                HLine {
                    x: 43.922
                    absolute: true
                },
                ClosePath {},
            ]
            fill: black
        }
            }},
       // today highs
        Group {
            transform: translate(0,bind todayHighsTY)
            content: 
        Text {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]
            opacity: bind todayNodesOpacity
            verticalAlignment: BASELINE
            content: bind weatherModel.todayHighs.toString()
            font: Font {faceName: 'Arial', style: PLAIN, size: 18.3347}
            x: 46
            y: 98.84
        }
            },
        // today lows
        Group {
            transform: translate(0,bind todayLowsTY)
            content:
        Text {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
            opacity: bind todayNodesOpacity
            verticalAlignment: BASELINE
            content: bind weatherModel.todayLows.toString()
            font: Font {faceName: 'Arial', style: PLAIN, size: 18.3347}
            x: 46.2
            y: 114.88
        }
            },
        // tomorrow highs
            Group {
                transform: translate( 0, bind tomorrowHighsTY)
                content:
        Text {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
            opacity: bind tomorrowNodesOpacity
            verticalAlignment: BASELINE
            content: bind weatherModel.tomorrowHighs.toString()
            font: Font {faceName: 'Arial', style: PLAIN, size: 43}
            x: 65
            y: 116
        }
                },
        // tomorrow lows
        Group {
            transform: translate( 0, bind tomorrowLowsTY)
            content:
        Text {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]
            
            opacity: bind tomorrowNodesOpacity
            verticalAlignment: BASELINE
            content: bind weatherModel.tomorrowLows.toString()
            font: Font {faceName: 'Arial', style: PLAIN, size: 43}
            x: 160
            y: 116
        }
                    },
        //]},
        
        // button lines
        Line {
            opacity: bind tomorrowNodesOpacity
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
            x1: 0
            x2: 90.369            
            y1: 128.248
            y2: 128.248
        },
        
        Line {
            opacity: bind todayNodesOpacity
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
            x1: 90.369
            x2: 233.345
            y1: 128.248
            y2: 128.248
        },

        Line {
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
            x1: 90.369
            x2: 90.369
            y1: 140.546
            y2: 128.248
        },

        // city name
        Group {
            filter: [ShadowFilter {x:1 y:-1 radius: 3}]
            content: [
        Text {
            verticalAlignment: BASELINE
            content: bind weatherModel.cityName
            font: Font {faceName: 'Arial', style: BOLD, size: 15.0}
            fill: rgba(0xF2, 0xF2, 0xF2, 0xff)
            transform: [
                matrix(1.0, 0.0, 0.0, 1.0, 6.145, 74.0625),
            ]
            x: 0.0
            y: 0.0
        },
        ]},
    ]}
]};        


var weatherModel = WeatherModel {
    cityName: "San Francisco"
    temperature: 45
    todayHighs: 94
    todayLows: 90
    windSpeed: 46
    windDirection: 259
    todayConditionCode: 1
    tomorrowConditionCode: 6
};

var weather = Weather{
    weatherModel: weatherModel
};

Canvas {
   content: weather
}



