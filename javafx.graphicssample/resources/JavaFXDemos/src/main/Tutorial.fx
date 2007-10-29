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
import java.io.File;

import shapes.*;
import painting.*;
import transformations.*;
import groups.*;
import swing.*;
import images.*;
import transparency.*;
import filters.*;
import events.*;
import areas.*;
import clipping.*;
import userdefined.*;
import animation.*;
import morphing.*;

class Tutorial extends CompositeNode {
    private attribute current: Node;
    private attribute source: String;
    private attribute menu:Menu;
    attribute height:Number;
    attribute width:Number;
    private attribute splitIndex:Number;
}

attribute Tutorial.height = 438;
attribute Tutorial.width = 872;
attribute Tutorial.splitIndex = 0.5;
attribute Tutorial.source = "main/Introduction.fx";
attribute Tutorial.menu = Menu{
    items:[MenuItem{name:"Introduction"
                action:operation(){current=Introduction;source="main/Introduction.fx";}
            },
          MenuItem{
            name:"Shapes"
            //action:operation(){current=Shapes{};}
            hasSubMenu:true
            subMenu:[MenuItem{
                        name:"Rect"
                        action:operation(){current=RectExample{};source="shapes/RectExample.fx";}
                     },
                     MenuItem{name:"Circle"
                        action:operation(){current=CircleExample{};source="shapes/CircleExample.fx";}
                     },
                     MenuItem{name:"Ellipse"
                        action:operation(){current=EllipseExample{};source="shapes/EllipseExample.fx";}
                     },
                     MenuItem{name:"Line"
                        action:operation(){current=LineExample{};source="shapes/LineExample.fx";}
                     },
                     MenuItem{name:"Polyline"
                        action:operation(){current=PolylineExample{};source="shapes/PolylineExample.fx";}
                     },
                     MenuItem{name:"Polygon"
                        action:operation(){current=PolygonExample{};source="shapes/PolygonExample.fx";}
                     },
                     MenuItem{name:"Arc"
                        action:operation(){current=ArcExample{};source="shapes/ArcExample.fx";}
                     },
                     MenuItem{name:"CubicCurve"
                        action:operation(){current=CubicCurveExample{};source="shapes/CubicCurveExample.fx";}
                     },
                     MenuItem{name:"QuadCurve"
                        action:operation(){current=QuadCurveExample{};source="shapes/QuadCurveExample.fx";}
                     },
                     MenuItem{name:"Star"
                        action:operation(){current=StarExample{};source="shapes/StarExample.fx";}
                     },
                     MenuItem{name:"Text"
                        action:operation(){current=TextExample{};source="shapes/TextExample.fx";}
                     },
                     MenuItem{name:"Path"
                        action:operation(){current=PathExample{};source="shapes/PathExample.fx";}
                     }
                ]   
        },
        MenuItem{name:"Painting"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"Stroke"
                        action:operation(){current=StrokeExample{};source="painting/StrokeExample.fx";}
                    },
                    MenuItem{name:"Fill"
                        action:operation(){current=FillExample{};source="painting/FillExample.fx";}
                    },
                    MenuItem{name:"LinearGradient"
                        action:operation(){current=LinearGradientExample{};source="painting/LinearGradientExample.fx";}
                    },
                    MenuItem{name:"RadialGradient"
                        action:operation(){current=RadialGradientExample{};source="painting/RadialGradientExample.fx";}
                    },
                    MenuItem{name:"Pattern"
                        action:operation(){current=PatternExample{};source="painting/PatternExample.fx";}
                    }
                ]},
        MenuItem{name:"Transformations"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=TransformationExample{};source="transformations/TransformationExample.fx";}
                    },
                    MenuItem{name:"Translate"
                        action:operation(){current=TransformationExample{transformation:translate(100, 20)};source="transformations/TranslateExample.fx";}
                    },
                    MenuItem{name:"Rotate"
                        action:operation(){current=TransformationExample{transformation:rotate(20, 0, 0)};source="transformations/RotateExample.fx";}
                    },
                    MenuItem{name:"Scale"
                        action:operation(){current=TransformationExample{transformation:scale(2.0, 2.0)};source="transformations/ScaleExample.fx";}
                    },
                    MenuItem{name:"Skew"
                        action:operation(){current=TransformationExample{transformation:skew(10, 10)};source="transformations/SkewExample.fx";}
                    }
                ]
        },
        MenuItem{name:"Groups"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=GroupsExample{};source="groups/NoneExample.fx";}
                    },
                    MenuItem{name:"Translate"
                        action:operation(){current=GroupsExample{transformation:translate(100, 20)};source="groups/TranslateExample.fx";}
                    },
                    MenuItem{name:"Rotate"
                        action:operation(){current=GroupsExample{transformation:rotate(20, 0, 0)};source="groups/RotateExample.fx";}
                    },
                    MenuItem{name:"Scale"
                        action:operation(){current=GroupsExample{transformation:scale(2.0, 2.0)};source="groups/ScaleExample.fx";}
                    },
                    MenuItem{name:"Skew"
                        action:operation(){current=GroupsExample{transformation:skew(10, 10)};source="groups/SkewExample.fx";}
                    }
                ]
        },
        MenuItem{name:"Swing Components"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=SwingExample{};source="swing/SwingExample.fx";}
                    },
                    MenuItem{name:"Translate"
                        action:operation(){current=SwingExample{transformation:translate(100, 20)};source="swing/TranslateExample.fx";}
                    },
                    MenuItem{name:"Rotate"
                        action:operation(){current=SwingExample{transformation:rotate(20, 0, 0)};source="swing/RotateExample.fx";}
                    },
                    MenuItem{name:"Scale"
                        action:operation(){current=SwingExample{transformation:scale(2.0, 2.0)};source="swing/ScaleExample.fx";}
                    },
                    MenuItem{name:"Skew"
                        action:operation(){current=SwingExample{transformation:skew(10, 10)};source="swing/SkewExample.fx";}
                    }
                ]
        },
        MenuItem{name:"Images"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=ImagesExample{};source="images/ImagesExample.fx";}
                    },
                    MenuItem{name:"Translate"
                        action:operation(){current=ImagesExample{transformation:translate(100, 20)};source="images/TranslateExample.fx";}
                    },
                    MenuItem{name:"Rotate"
                        action:operation(){current=ImagesExample{transformation:rotate(20, 0, 0)};source="images/RotateExample.fx";}
                    },
                    MenuItem{name:"Scale"
                        action:operation(){current=ImagesExample{transformation:scale(2.0, 2.0)};source="images/ScaleExample.fx";}
                    },
                    MenuItem{name:"Skew"
                        action:operation(){current=ImagesExample{transformation:skew(10, 10)};source="images/SkewExample.fx";}
                    }
                ]
        },
        MenuItem{name:"Transparency"
            action:operation(){current=TransparencyExample{};source="transparency/TransparencyExample.fx";}
        },
        MenuItem{name:"Filter Effects"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=FiltersExample{};source="filters/FiltersExample.fx";}
                    },
                    MenuItem{name:"Blur"
                        action:operation(){current=FiltersExample{currentFilter:GaussianBlur {radius: 6}};source="filters/BlurExample.fx";}
                    },
                    MenuItem{name:"Noise"
                        action:operation(){current=FiltersExample{currentFilter:Noise {distribution: 0}};source="filters/NoiseExample.fx";}
                    },
                    MenuItem{name:"Shadow"
                        action:operation(){current=FiltersExample{currentFilter:ShadowFilter};source="filters/ShadowExample.fx";}
                    },
                    MenuItem{name:"ShapeBurst"
                        action:operation(){current=FiltersExample{currentFilter:ShapeBurst};source="filters/ShapeBurstExample.fx";}
                    }]
        }, 
        MenuItem{name:"Input Events"
            action:operation(){current=InputEvents{};source="events/InputEvents.fx";}
        }, 
        MenuItem{name:"Areas"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=AreasNoneExample{};source="areas/AreasNoneExample.fx";}
                    },
                    MenuItem{name:"Add"
                        action:operation(){current=AddExample{};source="areas/AddExample.fx";}
                    },
                    MenuItem{name:"Subtract"
                        action:operation(){current=SubtractExample{};source="areas/SubtractExample.fx";}
                    },
                    MenuItem{name:"Intersect"
                        action:operation(){current=IntersectExample{};source="areas/IntersectExample.fx";}
                    },
                    MenuItem{name:"XOR"
                        action:operation(){current=XORExample{};source="areas/XORExample.fx";}
                    }]
        }, 
        MenuItem{name:"Clipping"
            hasSubMenu:true
                subMenu:[
                    MenuItem{name:"None"
                        action:operation(){current=NoneClipExample{};source="clipping/NoneClipExample.fx";}
                    },
                    MenuItem{name:"Rectangle"
                        action:operation(){current=RectangleExample;source="clipping/RectangleExample.fx";}
                    },
                    MenuItem{name:"Circle"
                        action:operation(){current=CircleClipExample{};source="clipping/CircleClipExample.fx";}
                    },
                    MenuItem{name:"Text"
                        action:operation(){current=TextClipExample{};source="clipping/TextClipExample.fx";}
                    }]
        },
        MenuItem{name:"User-Defined"
            action:operation(){current=GraphicObjects{
                                x1: 20
                                y1: 20
                                x2: 120
                                y2: 80
                                stroke: new Color(0.0, 0.0, 1.0, 0.5)
                               };source="userdefined/GraphicObjects.fx";}
        }, 
        MenuItem{name:"Animation"
            action:operation(){current=AnimationExample{};source="animation/AnimationExample.fx";}
        }, 
        MenuItem{name:"Shape Morphing"
            action:operation(){current=MorphExample{};source="morphing/MorphExample.fx";}
        }]
};


operation Tutorial.composeNode() {
    var x=0;
    var y=0;
    current = Introduction{};
    var topHeight = bind height*(1-splitIndex);
    var bottomHeight = bind height*splitIndex-18;
    return Group{
                content:[
                View{
                    transform:translate(menu.itemWidth+1,0)
                    content:
                    SplitPane{
                        onMouseDragged:operation(e:MouseEvent){
                            splitIndex = 1-e.y/height;
                            if (splitIndex>1){splitIndex=1;}
                            if (splitIndex<0){splitIndex=0;}
                        }
                        orientation:VERTICAL
                        background:Color{red: 0.06 green: 0.39 blue: 0.73}
                        content:
                            [SplitView{
                            content:
                            Canvas{
                            preferredSize:{width:bind width - menu.itemWidth,height:bind topHeight}
                            content:[
                            Rect{
                                x:0
                                y:0
                                width:bind width - menu.itemWidth-1
                                height:bind topHeight
                                fill:lightblue
                                scaleToFitCanvas:true
                            },
                            Group {
                                transform: bind [translate(50,0)]
                                content:bind [
                                    current
                                ]
                                scaleToFitCanvas:true
                            }
                        ]}},
                        SplitView{
                            content:Canvas{
                                content:
                                SourceView{
                                    transform:bind translate(5,5)
                                    width: bind width - menu.itemWidth-10
                                    title:bind source
                                    height:bind bottomHeight-5
                                    sourcePath: bind source
                                }
                        }}]
                    }
                },
                Rect{
                    x:0
                    y:0
                    width:menu.itemWidth+1
                    height:bind height
                    stroke:black
                    fill:Color{red: 0.06 green: 0.39 blue: 0.73}
                },
                menu
                ]
        };
}

Canvas{content:
Tutorial{}
}
