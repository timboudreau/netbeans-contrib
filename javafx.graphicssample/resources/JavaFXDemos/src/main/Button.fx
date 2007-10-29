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

package main;
import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.filter.*;

class Button extends CompositeNode, ActionWidget{
    attribute x: Number;
    attribute y: Number;
    attribute text: String;
    attribute font: Font;
    attribute width:Number;
    attribute selected:Boolean;
    attribute hover: Boolean;
    private attribute textOffset:Number;
    private attribute color1: Color;
    private attribute color2: Color;
    private attribute hoverColor1:Color;
    private attribute hoverColor2:Color;
    
}

attribute Button.textOffset=10;
trigger on Button.selected = newvalue {
    color1 = if (newvalue) then Color{red: 0.87 green: 0.14 blue: 0.13} 
                else Color{red: 0.06 green: 0.39 blue: 0.73};
    color2 = if(newvalue) then Color{red:0.78 green:0.14 blue: 0.13} 
                else Color{red:0.32 green:0.21 blue: 0.73};
    hoverColor1 = if (newvalue) then Color{red:1,green: 0,blue: 0} else Color{red:0.3, green: 0.48, blue: 0.73};
    hoverColor2 = if (newvalue) then Color {red: 1, green: 0.43, blue: 0} else Color {red: 0.4, green: 0.43, blue: 0.73};
    
}
operation Button.composeNode() {
    var textNode = Text {
        x: bind x+textOffset
        y: bind y+5
        content: bind text
        fill: white
        font: bind font
        
    };
    var cx = x+1;
    var cy = y+1;
    hover = false;
    color1 = if (selected) then Color{red: 0.87 green: 0.14 blue: 0.13} else Color{red: 0.06 green: 0.39 blue: 0.73};
    color2 = if(selected) then Color{red:0.78 green:0.14 blue: 0.13} else Color{red:0.32 green:0.21 blue: 0.73};
    hoverColor1 = if (selected) then Color{red:1,green: 0,blue: 0} else Color{red:0.3, green: 0.48, blue: 0.73};
    hoverColor2 = if (selected) then Color {red: 1, green: 0.43, blue: 0} else Color {red: 0.4, green: 0.43, blue: 0.73};
    
    return Group{
        content: [
        Rect {
            //arcHeight: 10
            //arcWidth: 10
            x: bind cx
            y: bind cy
            width: bind if (width<>0) then width else textNode.currentWidth+2*textOffset
            height: bind textNode.currentHeight+10
            stroke: black
            fill: black
            
        },
        Rect{
            //arcHeight: 10
            //arcWidth: 10
            x: bind x
            y: bind y
            width: bind if (width<>0) then width else textNode.currentWidth+2*textOffset
            height: bind textNode.currentHeight+10
            fill: LinearGradient {
                x1:0, y1:0.25, x2:0, y2:0.75
                stops:
                    [Stop {
                    offset: 0
                    color: bind if(hover) then hoverColor1 else color1 //rgb(16,100,186)
                },
                Stop {
                    offset:0.1
                    color: bind if(hover) then hoverColor2 else color2//rgb(81,55,186)
                },
                Stop {
                    offset:0.9
                    color: bind if(hover) then hoverColor1 else color1//rgb(81,55,186)
                }]
            }
            stroke: black
            cursor: HAND
            onMouseClicked: operation(e:CanvasMouseEvent) {
                if (action<> null){
                    (action)();
                }
            }
            onMousePressed: operation(e:CanvasMouseEvent) {
                x = x + 1;
                cx = cx - 1;
                y = y + 1;
                cy = cy - 1;
            }
            onMouseReleased: operation(e:CanvasMouseEvent) {
                x=x-1;
                cx = cx + 1;
                y=y-1;
                cy = cy + 1;
            }
            onMouseEntered: operation(e:CanvasMouseEvent) {
                hover = true;
            }
            onMouseExited: operation(e:CanvasMouseEvent) {
                hover = false;
            }
        },
        textNode]
    };
}

Button{
    var text = "Button"
    x:0
    y:0
    text: bind text
    font: new Font("Arial", "BOLD", 30)
    toolTipText: "Button"
}