package math;

import javafx.gui.*;

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
        content : [
            Rectangle {
                width : 200, height : 200
                fill : Color.LIGHTGREY
                
                onMouseMoved : function( e : MouseEvent ) {
                    for( eye in eyes ) {
                        eye.mouse = e;
                    }
                }
            },
            eyes
        ]
    }
    
    visible : true
    title : "Arctangent"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

class Eye extends CustomNode {
    
    public attribute x : Number;
    public attribute y : Number;
    
    public attribute size : Number;
    
    public attribute mouse : MouseEvent;
    attribute angle : Number = bind Math.toDegrees( Math.atan2( y - mouse.getY(), x - mouse.getX())) + 180;
    
    public function create(): Node {
        return Group {
            transform : Translate { x : bind x, y : bind y }
            content : [
                Circle {
                    radius : bind size
                    fill : Color.WHITE
                },
                Circle {
                    transform : Rotate { angle : bind angle }
                    centerX : bind size / 2, centerY : 0, radius : bind size / 2
                    fill : Color.rgb( 153, 153, 153 )
                }
            ]
        };
    }
}
