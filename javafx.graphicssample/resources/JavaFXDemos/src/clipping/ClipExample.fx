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

package clipping;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.filter.*;

class ClipExample extends CompositeNode{
    attribute shape:Node;
}

function ClipExample.composeNode() = Group {
    content: Clip {
        shape: bind shape
        content: 
        [Rect {
            x: 20
            y: 20
            height: 80
            width: 300
            arcHeight: 20
            arcWidth: 20
            fill: cyan
            stroke: purple
            strokeWidth: 2
        },
        Ellipse {
            cx: 150
            cy: 80
            radiusX: 100
            radiusY: 50
            fill: orange
            stroke: blue
            strokeWidth: 2
        },
        View {
            transform: translate(150, 70)
            content: Button {
               cursor: DEFAULT
               text: "Click Me!"
            }
        },
        Polygon {
             points: [5, 5, 25, 5, 15, 25]
             fill: gray
             stroke: black
             strokeWidth: 3
        },
        View {
            transform: translate(100, 40)
            content: TextField {
                columns: 15
                value: "This is a text field"
            }
        }]
    }
};


Canvas {
    var clip = ClipExample{}
    content: [clip,
        View{
            transform:translate(350,0)
            content: Label {
                text:
"<html> 
   <body>
       <p>Click the buttons below to see the effect of several different shapes.</p>                
   </body>
</html>"                
                                    
            }
        },View {
            transform:translate(370,20)
            content:
                GroupPanel{
                    cursor: DEFAULT
                    var row1 = Row {alignment: BASELINE}
                    var row2 = Row {alignment: BASELINE}
                    var row3 = Row {alignment: BASELINE}
                    var column1 = Column { }
                    rows: [row1, row2, row3]
                    columns: [column1]
                    content:[
                        Button{
                            preferredSize: {width: 120}
                            row:row1
                            column: column1
                            text: "Rectangle"
                            opaque: false
                            action: operation(){clip.shape = Rect { x: 40, y: 40, width: 220, height: 80};}
                        },
                        Button{
                            preferredSize: {width: 120}
                            row:row2
                            column: column1
                            text: "Circle"
                            opaque: false
                            action: operation(){clip.shape = Circle { cx: 150, cy: 80, radius: 60 };}
                        },
                        Button{
                            preferredSize: {width: 120}
                            row:row3
                            column: column1
                            text: "Text"
                            opaque: false
                            action: operation(){clip.shape = Text { x: 30, y: 30, font: new Font("Verdana", "BOLD", 100), content: "Text" };}
                        }
                    ]
                }}
    ]
}
