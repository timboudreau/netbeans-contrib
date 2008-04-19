package color;

import javafx.gui.*;
import java.lang.System;

var B1 = Color.rgb( 190, 190, 190 );
var B2 = Color.rgb(  20,  20,  20 );

var C1 = Color.rgb( 255, 120,   0 );
var C2 = Color.rgb(  10,  45, 255 );
var C3 = Color.rgb(  10, 255,  15 );
var C4 = Color.rgb( 125,   2, 140 );
var C5 = Color.rgb( 255, 255,   0 );
var C6 = Color.rgb(  25, 255, 200 );

var Y_AXIS = false;
var X_AXIS = true;
        
var l1  =         
        
Frame {
    content : Canvas {
        content: [
            GradientBox { x :   0, y :   0, size : 200, c1 : B1, c2 : B2, axis : Y_AXIS },
            GradientBox { x :  25, y :  25, size :  75, c1 : C1, c2 : C2, axis : Y_AXIS },
            GradientBox { x : 100, y :  25, size :  75, c1 : C3, c2 : C4, axis : X_AXIS },
            GradientBox { x :  25, y : 100, size :  75, c1 : C2, c2 : C5, axis : X_AXIS },
            GradientBox { x : 100, y : 100, size :  75, c1 : C4, c2 : C6, axis : Y_AXIS }
       ]
    }
    
    visible : true
    title : "Gradient Sample"
    width : 208
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

class GradientBox extends CustomNode {
    attribute x : Number;
    attribute y : Number;
    attribute size : Number;
    attribute c1 : Color;
    attribute c2 : Color;
    attribute axis : Boolean;
    
    private attribute fill : LinearGradient;
    private attribute node : Node;
   
    init {
        var xx : Number;
        var yy : Number;
        if( axis ) { 
            xx = 1; yy = 0.1;
        } else { 
            xx = 0.1; yy = 1;
        }
        fill = LinearGradient {
           startX : 0, startY : 0, 
           endX : bind xx, endY : bind yy
           stops: [
               Stop { offset: 0, color: bind c1 },
               Stop { offset: 1, color: bind c2 }
           ]
        };
    }
    
    function create() : Node {
        return Group {
            content : Rectangle {
                x: bind x
                y: bind y
                width: bind size
                height: bind size
                fill: bind fill
            }
        };
    }   
}
