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

package filters;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.filter.*;

class FiltersExample extends CompositeNode{
    attribute currentFilter: Filter*;
}

function FiltersExample.composeNode() = Group {
        filter: bind currentFilter
        content: 
        [Star {
            cx: 100
            cy: 100
            points: 5
            startAngle: 18
            rin: 30
            rout: 70
            fill: blue
    
        },
        Text {
            x: 200
            y: 20
            content: "5-point Star"
            font: new Font("Verdana", "BOLD", 60)
            fill: red
        }]
    };
    
Canvas {
    var node = FiltersExample{
        transform:translate(120,40)
    }
    content:[node,
        View {
            content: Label{
                text:
"<html> 
   <body>
       <p>Click the buttons below to see the effect of several different shapes.</p>                
   </body>
</html>"                
            }
        },View {
            transform:translate(0,20)
            content:
                GroupPanel{
                    cursor: DEFAULT
                    var row1 = Row {alignment: BASELINE}
                    var row2 = Row {alignment: BASELINE}
                    var row3 = Row {alignment: BASELINE}
                    var row4 = Row {alignment: BASELINE}
                    var row5 = Row {alignment: BASELINE}
                    var column1 = Column { }
                    rows: [row1, row2, row3, row4, row5]
                    columns: [column1]
                    content:[
                        Button{
                            preferredSize: {width: 110}
                            row:row1
                            column: column1
                            text: "None"
                            opaque: false
                            action: operation(){node.currentFilter = [];}
                        },
                        Button{
                            preferredSize: {width: 110}
                            row:row2
                            column: column1
                            text: "Shadow"
                            opaque: false
                            action: operation(){node.currentFilter = ShadowFilter;}
                        },
                        Button{
                            preferredSize: {width: 110}
                            row:row3
                            column: column1
                            text: "Blur"
                            opaque: false
                            action: operation(){node.currentFilter = GaussianBlur {radius: 6};}
                        },
                        Button{
                            preferredSize: {width: 110}
                            row:row4
                            column: column1
                            text: "Noise"
                            opaque: false
                            action: operation(){node.currentFilter = Noise {distribution: 0};}
                        },
                        Button{
                            preferredSize: {width: 110}
                            row:row5
                            column: column1
                            text: "ShapeBurst"
                            opaque: false
                            action: operation(){node.currentFilter =ShapeBurst;}
                        }
                    ]}
        }
    ]
}