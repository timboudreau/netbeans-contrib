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

package animation;

import javafx.ui.*;
import javafx.ui.canvas.*;


class AnimationExample extends CompositeNode {
    attribute width: Number;
    attribute height: Number;
    attribute opacityValue: Number;
    attribute rotationValue: Number;
}
// Initial values
attribute AnimationExample.width = 200;
attribute AnimationExample.height = 100;
attribute AnimationExample.opacityValue = 1.0;
attribute AnimationExample.rotationValue = 0;

function AnimationExample.composeNode() = 

    Group {
        content:
        [Rect {
            width: bind width
            height: bind height
            arcHeight: 20
            arcWidth: 20
            opacity: bind opacityValue
            transform: bind [translate(80, 50), rotate(rotationValue, width/2, height/2)]
            fill: dodgerblue
            stroke: darkblue
            strokeWidth: 2
        },
        View {
            content: GroupPanel {
                cursor: DEFAULT
                var row = Row {alignment: BASELINE}
                var column1 = Column { }
                var column2 = Column { }
                var column3 = Column { }
                var column4 = Column { }
				var column5 = Column { }
                rows: [row]
                columns: [column1, column2, column3, column4, column5]
                content:
                [SimpleLabel {
                    row: row
                    column: column1
                    text: "Animate:"
                },
                Button {
                    row: row
                    column: column2
                    opaque: false
                    mnemonic: W
                    text: "Width"
                    action: operation() {
                        width = [0..200] dur 1000;
                    }
                },
                Button {
                    row: row
                    column: column3
                    opaque: false
                    mnemonic: H
                    text: "Height"
                    action: operation() {
                        height = [0..80] dur 1000;
                    }
                },
                Button {
                    row: row
                    column: column4
                    opaque: false
                    mnemonic: O
                    text: "Opacity"
                    action: operation() {
                        opacityValue = [0.00,0.01 .. 1.00] dur 1000;
                    }
                },
                Button {
                    row: row
                    column: column5
                    opaque: false
                    mnemonic: R
                    text: "Rotation"
                    action: operation() {
                        rotationValue = [0..360] dur 1000;
                    }
                }]
            }
        }]
    }
;


Canvas {
    content: AnimationExample {
    }
}
