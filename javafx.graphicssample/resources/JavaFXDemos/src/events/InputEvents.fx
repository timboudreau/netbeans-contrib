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

package events;

import javafx.ui.canvas.*;
import javafx.ui.*;

class InputEvents extends CompositeNode{
    private function displayMouseEvent(title:String, e:CanvasMouseEvent);
}

    function InputEvents.displayMouseEvent(title:String, e:CanvasMouseEvent) {
        return 
        "<html>
           <table>
              <tr><td><b>{title}</b></td></tr>
              <tr>
                 <td>button:</td>
                 <td>{e.button}</td>
              </tr>
              <tr>
                 <td>clickCount:</td>
                 <td>{e.clickCount}</td>
              </tr>
              <tr>
                 <td>x:</td>
                 <td>{e.x}</td>
              </tr>
              <tr>
                 <td>y:</td>
                 <td>{e.y}</td>
              </tr>
              <tr>
                 <td>localX:</td>
                 <td>{e.localX}</td>
              </tr>
              <tr>
                 <td>localY:</td>
                 <td>{e.localY}</td>
              </tr>
              {if (e.dragTranslation <> null) 
                then
                "<tr>
                   <td>dragX:</td>
                   <td>{e.dragTranslation.x}</td>
                </tr>
                <tr>
                   <td>dragY:</td>
                   <td>{e.dragTranslation.y}</td>
                </tr>
                <tr>
                   <td>localDragX:</td>
                   <td>{e.localDragTranslation.x}</td>
                </tr>
                <tr>
                   <td>localDragY:</td>
                   <td>{e.localDragTranslation.y}</td>
                </tr>"
                else
                ""}
          </table>
        </html>";
    }
operation InputEvents.composeNode() {
    var label = Label {};
    return Group{
        content:[View {
            transform: translate(200, 10)
            content: label
        },
        Group {
            transform: [translate(30, 30), rotate(45, 50, 50)]
            content: Circle {
                cx: 50
                cy: 50
                radius: 50
                fill: yellow
                stroke: green
                strokeWidth: 2
                onMouseClicked: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Clicked", e);
                }
                onMousePressed: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Pressed", e);
                }
                onMouseEntered: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Entered", e);
                }
                onMouseExited: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Exited", e);
                }
                onMouseMoved: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Moved", e);
                }
                onMouseReleased: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Released", e);
                }
                onMouseDragged: operation(e:CanvasMouseEvent) {
                    label.text = displayMouseEvent("Mouse Dragged", e);
                }
            }
    }]};
}
InputEvents{}