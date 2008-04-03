package math;

import javafx.ui.*;
import javafx.ui.canvas.*;

import java.lang.Math;

var eyes : Eye[] = [
    Eye { x : 50, y : 16, size : 40 },
    Eye { x : 64, y : 85, size : 20 },  
    Eye { x : 90, y : 200, size : 60 },
    Eye { x : 150, y : 44, size : 20 }, 
    Eye { x : 175, y : 120,  size : 40 }
];
    
Frame {
    content : Canvas {
        background : Color.LIGHTGREY
        content : eyes

        onMouseMoved : function( e : MouseEvent ) {
            for( eye in eyes ) {
                eye.mouse = e;
            }
        }
    }
    
    visible : true
    title : "Arctangent"
    width : 200
    height : 232
}

class Eye extends CompositeNode {
    
    public attribute x : Number;
    public attribute y : Number;
    
    public attribute size : Number;
    
    public attribute mouse : MouseEvent;
    attribute angle : Number = bind Math.toDegrees( Math.atan2( y - mouse.y, x - mouse.x )) + 180;
    
    public function composeNode(): Node {
        return Group {
            transform : Translate { x : x, y : y }
            content : [
                Circle {
                    radius : size
                    fill : Color.WHITE
                },
                Circle {
                    transform : Rotate { angle : bind angle }
                   cx : size / 2, cy : 0, radius : size / 2
                   fill : Color.rgb( 153, 153, 153 )
                }
            ]
        };
    }
}
