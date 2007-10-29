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

class MenuItem {
    attribute name: String;
    attribute subMenu: MenuItem*;
    attribute hasSubMenu:Boolean;
    attribute action: function():*;
}

attribute MenuItem.hasSubMenu=false;

class Menu extends CompositeNode{
    attribute items:MenuItem*;
    attribute x: Number;
    attribute y: Number;
    attribute itemWidth:Number;
    private attribute menuIndex:Number;
    private attribute subMenuIndex:Number;
    private attribute subMenu:Node*;
    private attribute selectedItem:Number;
}
attribute Menu.selectedItem=0;
attribute Menu.x=0;
attribute Menu.y=0;
attribute Menu.itemWidth = 150;
attribute Menu.items=[
        MenuItem{name:"Introduction"},
        MenuItem{
            name:"Shapes"
            hasSubMenu:true
            subMenu:[MenuItem{name:"Rect"},
                     MenuItem{name:"Circle"},
                     MenuItem{name:"Ellipse"},
                     MenuItem{name:"Line"},
                     MenuItem{name:"Polyline"},
                     MenuItem{name:"Polygon"},
                     MenuItem{name:"Arc"},
                     MenuItem{name:"CubicCurve"},
                     MenuItem{name:"QuadCurve"},
                     MenuItem{name:"Star"},
                     MenuItem{name:"Text"},
                     MenuItem{name:"Path"}
                ]   
        },
        MenuItem{name:"Painting"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"Stroke"},
                    MenuItem{name:"Fill"},
                    MenuItem{name:"LinearGradient"},
                    MenuItem{name:"RadialGradient"},
                    MenuItem{name:"Pattern"}
                ]},
        MenuItem{name:"Transformations"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"},
                    MenuItem{name:"Transplate"},
                    MenuItem{name:"Rotate"},
                    MenuItem{name:"Scale"},
                    MenuItem{name:"Skew"}
                ]
        },
        MenuItem{name:"Groups"},
        MenuItem{name:"Swing Components"},
        MenuItem{name:"Images"},
        MenuItem{name:"Transparency"},
        MenuItem{name:"Filter Effects"}, 
        MenuItem{name:"Input Events"}, 
        MenuItem{name:"Areas"}, 
        MenuItem{name:"Clipping"},
        MenuItem{name:"User-Defined"}, 
        MenuItem{name:"Animation"}, 
        MenuItem{name:"Shape Morphing"}];

trigger on Menu.subMenuIndex = newvalue{
    if(newvalue==-1) {
        subMenu = [];
    }
}

operation Menu.composeNode() {
    menuIndex = -1;
    subMenuIndex=-1;
    var inSubMenu = false;
    var entered=0;
    return Group{
        content:bind [
            foreach(i in [0..sizeof items-1]) 
                Button{
                    x: x
                    y: y+i*20
                    width: itemWidth
                    text: items[i].name
                    selected: bind if (selectedItem==i) then true else false
                    action:operation(){
                        selectedItem=i;
                        if (items[i].action<>null){
                            (items[i].action)();
                        }
                        if (items[i].hasSubMenu){
                            menuIndex = i;
                            subMenu = Group{
                            content:[
                                foreach(j in [0..sizeof items[i].subMenu-1])
                                Button{
                                    x:x+itemWidth
                                    y:y+j*20+i*20
                                    width:itemWidth
                                    text: items[i].subMenu[j].name
                                    action:operation(){
                                        subMenuIndex=-1;
                                        if(items[i].subMenu[j].action<>null){
                                            (items[i].subMenu[j].action)();
                                        }
                                    }
                                    onMouseEntered:operation(e:CanvasMouseEvent){
                                        subMenuIndex=j;
                                    }
                                }]};
                                
                            
                        } else {
                            menuIndex=-1;
                            subMenu =[];
                        }
                    }
                    onMouseEntered:operation(e:CanvasMouseEvent){
                        if (menuIndex<>i){
                            subMenuIndex=-1;
                            subMenu=[];
                        }
                    }
                },
                subMenu
        ]
    };
}

Menu{}