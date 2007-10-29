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
package userdefined;

import javafx.ui.*;
import javafx.ui.canvas.*;


class GraphicObjects extends CompositeNode {
    attribute x1: Number;
    attribute y1: Number;
    attribute x2: Number;
    attribute y2: Number;
    attribute stroke: Paint;
}

function GraphicObjects.composeNode() = 

    Group {
        content:
        [Line {
            x1: bind x1
            y1: bind y1
            x2: bind x2
            y2: bind y2
            stroke: bind stroke
            strokeWidth: 2
        },
        Circle {
            cursor: HAND
            cx: bind x1
            cy: bind y1
            radius: 5
            stroke: bind stroke
            strokeWidth: 2
            fill: new Color(0, 0, 0, 0)
            onMouseDragged: operation(e:CanvasMouseEvent) {
                x1 += e.localDragTranslation.x;
                y1 += e.localDragTranslation.y;
            }
        },
        Circle {
            cursor: HAND
            cx: bind x2
            cy: bind y2
            radius: 5
            stroke: bind stroke
            strokeWidth: 2
            fill: new Color(0, 0, 0, 0)
            onMouseDragged: operation(e:CanvasMouseEvent) {
                x2 += e.localDragTranslation.x;
                y2 += e.localDragTranslation.y;
            }
        }]
    }
;


Canvas {
    content: GraphicObjects {
        x1: 20
        y1: 20
        x2: 120
        y2: 80
        stroke: new Color(0.0, 0.0, 1.0, 0.5)
    }
}
