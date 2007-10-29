
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
 * LoadingScreen.fx
 *
 * Created on Oct 15, 2007, 10:48:12 PM
 */

package weatherfx;

import javafx.ui.UIElement;
import javafx.ui.*;
import javafx.ui.canvas.*;


/**
 * Class holding graphics data for the loading screen. Original imported from Adobe Illustrator
 * using SVG export and SVG -> JavaFX converter.
 * 
 * @author breh
 */


public class LoadingScreen extends CompositeNode {    
    public function SVGID_1_(): LinearGradient;
}



function LoadingScreen.SVGID_1_() = [
    LinearGradient {
        gradientUnits: USER_SPACE_ON_USE
        x1: 117.1709
        x2: 117.1709
        y1: 77.7261
        y2: -6.5569
        stops: [
            Stop {
                offset: 0.0
                color: rgba(0x1A, 0x1A, 0x1A, 0xff)
            },
            Stop {
                offset: 0.2234
                color: rgba(0x0F, 0x0F, 0x0F, 0xff)
            },
            Stop {
                offset: 0.6046
                color: rgba(0x04, 0x04, 0x04, 0xff)
            },
            Stop {
                offset: 1.0
                color: rgba(0x00, 0x00, 0x00, 0xff)
            },
        ]
    },
];

function LoadingScreen.composeNode() = 
Group { content:[
        Path {
            d: [
                MoveTo {
                    x: 233.841
                    y: 75.336
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
                    y: 7.576
                    absolute: true
                },
                CurveTo {
                    x1: 0.5
                    y1: 3.667
                    x2: 2.016
                    y2: 0.5
                    x3: 3.887
                    y3: 0.5
                    smooth: false
                    absolute: true
                },
                HLine {
                    x: 226.571
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
                    y: 75.336
                    absolute: true
                },
                ClosePath {},
            ]
            fill: SVGID_1_()
            stroke: rgba(0x00, 0x00, 0x00, 0xff)
        },
        Text {
            verticalAlignment: BASELINE
            content: 'looking at the sky....'
            font: Font {faceName: 'Arial', style: BOLD, size: 12.0}
            fill: rgba(0xFF, 0xFF, 0xFF, 0xff)
            transform: [
                matrix(1.0, 0.0, 0.0, 1.0, 4.3374, 77.7271),
            ]
            x: 0.0
            y: 0.0
        },
        /*
        Text {
            verticalAlignment: BASELINE
            content: 'once loaded the weather would be nice'
            x: 0.0
            y: 0.0
        },
        Text {
            verticalAlignment: BASELINE
            content: 'to fade to the weather layout...'
            x: 0.0
            y: 14.4
        },*/
]};
LoadingScreen {}