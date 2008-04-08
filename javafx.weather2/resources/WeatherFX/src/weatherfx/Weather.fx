
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
    
    // business logic
    private attribute weatherModel: WeatherModel on replace {
        /*
        loadingScreenOpacity = [1.0,0.99..0.0] dur 1000;
        weatherScreenOpacity = [0.0,0.01..1.0] dur 1000;
         */
    };

    // animation support 
    private attribute todayNodesOpacity: Number = 1.0;
    private attribute tomorrowNodesOpacity: Number = bind 1.0 - todayNodesOpacity;
    
    private attribute lowsTriangleTransform:TransformHelper = TransformHelper {};
    private attribute highsTriangleTransform:TransformHelper = TransformHelper {
        scale: bind lowsTriangleTransform.scale
    };
    
    private attribute todayHighsTY:Number;
    private attribute todayLowsTY:Number;
    private attribute tomorrowHighsTY:Number = 0;
    private attribute tomorrowLowsTY:Number = 0;
    
    private attribute loadingScreenOpacity:Number = 1.0;
    private attribute weatherScreenOpacity:Number = 0.0;


    function getConditionImageForCode( conditionImages: ImageView[], code: Integer ): ImageView {
        return conditionImages[code];
    }

    attribute todayConditionImages : ImageView[] = [
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

    attribute tomorrowConditionImages : ImageView[] = [
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

    function createTodayClickableArea() {
        return Rect {
            x:0 y:129 width: 90 height:12
            fill: Color.WHITE
            opacity: 0.01
            onMousePressed: function( e: CanvasMouseEvent ): Void {        
                /*
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
                 */
            }
        };
    }

    function createTomorrowClickableArea() {
        return Rect {
            x:90 y:129 width: 145 height:12 
            fill: Color.WHITE
            opacity: 0.01
            onMousePressed: function( e: CanvasMouseEvent ): Void {                       
                /*
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
                 */
            }        
        };
    }

    function weatherGradient(): LinearGradient {
        return LinearGradient {
            transform: [
                Matrix.matrix(1.0, 0.0, 0.0, -1.0, 22.52, -15.1484),
            ]
            startX: 94.6499
            endX: 94.6499
            startY: -151.0098
            endY: -66.7269
            stops: [
                Stop {
                    offset: 0.0
                    color: Color.rgba( 0x10, 0x72, 0xBA, 0xff )
                },
                Stop {
                    offset: 0.0835
                    color: Color.rgba(0x4F, 0x8C, 0xC5, 0xff)
                },
                Stop {
                    offset: 0.1685
                    color: Color.rgba(0x70, 0xA5, 0xD1, 0xff)
                },
                Stop {
                    offset: 0.254
                    color: Color.rgba(0x8B, 0xBB, 0xDA, 0xff)
                },
                Stop {
                    offset: 0.3389
                    color: Color.rgba(0x9E, 0xCC, 0xE1, 0xff)
                },
                Stop {
                    offset: 0.423
                    color: Color.rgba(0xAA, 0xD6, 0xE5, 0xff)
                },
                Stop {
                    offset: 0.5055
                    color: Color.rgba(0xAF, 0xDB, 0xE7, 0xff)
                },
                Stop {
                    offset: 0.5692
                    color: Color.rgba(0xA8, 0xD4, 0xE4, 0xff)
                },
                Stop {
                    offset: 0.6617
                    color: Color.rgba(0x94, 0xC3, 0xDD, 0xff)
                },
                Stop {
                    offset: 0.772
                    color: Color.rgba(0x76, 0xAA, 0xD3, 0xff)
                },
                Stop {
                    offset: 0.8948
                    color: Color.rgba(0x4F, 0x8B, 0xC5, 0xff)
                },
                Stop {
                    offset: 1.0
                    color: Color.rgba(0x10, 0x72, 0xBA, 0xff)
                },
            ]
        };
    };


    // outline shape used for clipping the top weather node
    attribute outlineShape : Shape =  Path {
            transform: Translate{ x: 10, y: -57 }
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
            stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
        };



    function composeNode() : Node {
        return 
        Group {
            //clip: Clip { shape: Rect {x:0 y:0 width: 245 height: 85}}    
            clip: Clip { shape: outlineShape}    
            content: [
                Group {
                    transform: Translate{ x: 10, y: 1 }
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
            var weekdaysFont = Font {faceName: 'Arial', style: FontStyle.BOLD, size: 8.6149}
            opacity: bind weatherScreenOpacity
            //opacity: 0
            transform: Translate{ x: 10, y: -57 }
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
                    stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
                },



                Text {
                    verticalAlignment: Alignment.BASELINE
                    content: 'TODAY'
                    font: weekdaysFont
                    fill: Color.rgba(0xF2, 0xF2, 0xF2, 0xff)
                    transform: [
                        Matrix.matrix(1.0, 0.0, 0.0, 1.0, 6.6519, 137.2471),
                    ]
                    x: 0.0
                    y: 0.0
                },        
                createTodayClickableArea(),

                Text {
                    verticalAlignment: Alignment.BASELINE
                    content: 'TOMORROW'
                    font: weekdaysFont
                    fill: Color.rgba(0xF2, 0xF2, 0xF2, 0xff)
                    transform: [
                        Matrix.matrix(1.0, 0.0, 0.0, 1.0, 96.0806, 137.248),
                    ]
                    x: 0.0
                    y: 0.0
                },
                createTomorrowClickableArea(), 
                // today condition
                Group { 
                    transform: Translate{ y: 58 }
                    content: bind getConditionImageForCode(todayConditionImages, weatherModel.todayConditionCode)
                    opacity: bind todayNodesOpacity/2.0
                },
                // tomorrow condition
                Group { 
                    transform: Translate{ y: 58 }
                    content: bind getConditionImageForCode(tomorrowConditionImages, weatherModel.tomorrowConditionCode)
                    opacity: bind tomorrowNodesOpacity/2.0
                },



                // wind information
                Group {
                    opacity: bind todayNodesOpacity
                    content: 
                Group {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]
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
                    fill: Color.BLACK
                },
                Text {            
                    verticalAlignment: Alignment.BASELINE
                    //halign: CENTER
                    content: bind weatherModel.windInformation()
                    font: Font {faceName: 'Arial', style: FontStyle.PLAIN, size: 13.5526}
                    transform: [
                        //rotate (67,178,90)
                        Matrix.matrix(0.3866, 0.9865, -0.9074, 0.4203, 178.8594, 90.377),
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
                    stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
                },

                // temperature
                Group {
                    opacity: bind todayNodesOpacity
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]
                    content: [
                Circle {
                    cx: 148.475
                    cy: 85.083
                    radius: 2.45
                    stroke: Color.rgba(0xED, 0x22, 0x27, 0xff)
                    strokeWidth: 2.0
                },
                Text {
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.temperature.toString()
                    font: Font {faceName: 'Arial', style: FontStyle.BOLD, size: 48.6668}
                    fill: Color.rgba(0xED, 0x1E, 0x24, 0xff)
                    transform: [
                        Matrix.matrix(1.0, 0.0, 0.0, 1.0, 88.8457, 116.3926),
                    ]
                    x: 0.0
                    y: 0.0
                },]
                },


                Group {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
                    content: 
                Group {
                    transform: [ Translate{ x : bind highsTriangleTransform.tx, y : bind highsTriangleTransform.ty },
                        Translate{ x : 37, y : 93 }, 
                        Scale{ x: bind highsTriangleTransform.scale, y : bind highsTriangleTransform.scale }, 
                        Translate{ x: -37, y :-93 }]
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
                    fill: Color.BLACK
                }
                ]}},
                // lows triangle
                Group {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
                    content:        
                Group {
                    transform: [ Translate{ x : bind lowsTriangleTransform.tx, y : bind lowsTriangleTransform.ty },
                        Translate{ x: 37, y: 109 }, 
                        Scale{ x : bind lowsTriangleTransform.scale, y : bind lowsTriangleTransform.scale }, 
                        Translate{ x: -37, y: -109 }]            
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
                    fill: Color.BLACK
                }
                    }},
               // today highs
                Group {
                    transform: Translate { y : bind todayHighsTY }
                    content: 
                Text {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]
                    opacity: bind todayNodesOpacity
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.todayHighs.toString()
                    font: Font {faceName: 'Arial', style: FontStyle.PLAIN, size: 18.3347}
                    x: 46
                    y: 98.84
                }
                    },
                // today lows
                Group {
                    transform: Translate{ y : bind todayLowsTY }
                    content:
                Text {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
                    opacity: bind todayNodesOpacity
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.todayLows.toString()
                    font: Font {faceName: 'Arial', style: FontStyle.PLAIN, size: 18.3347}
                    x: 46.2
                    y: 114.88
                }
                    },
                // tomorrow highs
                    Group {
                        transform: Translate{ y: bind tomorrowHighsTY }
                        content:
                Text {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]            
                    opacity: bind tomorrowNodesOpacity
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.tomorrowHighs.toString()
                    font: Font {faceName: 'Arial', style: FontStyle.PLAIN, size: 43}
                    x: 65
                    y: 116
                }
                        },
                // tomorrow lows
                Group {
                    transform: Translate{ y : bind tomorrowLowsTY }
                    content:
                Text {
                    // FIXME: Not implemented yet
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]

                    opacity: bind tomorrowNodesOpacity
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.tomorrowLows.toString()
                    font: Font {faceName: 'Arial', style: FontStyle.PLAIN, size: 43}
                    x: 160
                    y: 116
                }
                            },
                //]},

                // button lines
                Line {
                    opacity: bind tomorrowNodesOpacity
                    stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
                    x1: 0
                    x2: 90.369            
                    y1: 128.248
                    y2: 128.248
                },

                Line {
                    opacity: bind todayNodesOpacity
                    stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
                    x1: 90.369
                    x2: 233.345
                    y1: 128.248
                    y2: 128.248
                },

                Line {
                    stroke: Color.rgba(0x00, 0x00, 0x00, 0xff)
                    x1: 90.369
                    x2: 90.369
                    y1: 140.546
                    y2: 128.248
                },

                // city name
                Group {
                    // FIXME: Not implemented yet                    
                    //filter: [ShadowFilter {x:1 y:-1 radius: 3}]
                    content: [
                Text {
                    verticalAlignment: Alignment.BASELINE
                    content: bind weatherModel.cityName
                    font: Font {faceName: 'Arial', style: FontStyle.BOLD, size: 15.0}
                    fill: Color.rgba(0xF2, 0xF2, 0xF2, 0xff)
                    transform: [
                        Matrix.matrix(1.0, 0.0, 0.0, 1.0, 6.145, 74.0625),
                    ]
                    x: 0.0
                    y: 0.0
                },
                ]},
            ]}
        ]};        
    }
}

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



