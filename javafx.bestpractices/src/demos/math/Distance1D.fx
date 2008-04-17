package math;

import javafx.gui.*;
import javafx.animation.*;

var boxes : Box[] = [
    Box {
        xpos : 134, ypos : 0, thickness : 36, direction : 1.0, div : 64, 
        color : Color.rgb( 102, 102, 102 )
    },
    Box {
        xpos : 44, ypos : 0, thickness : 8, direction : 1.0, div : 16, 
        color : Color.rgb( 204, 204, 204 )
    },
    Box {
        xpos : 58, ypos : 100, thickness : 36, direction : -1.0, div : 64, 
        color : Color.rgb( 102, 102, 102 )
    },
    Box {
        xpos : 120, ypos : 100, thickness : 8, direction : -1.0, div : 16, 
        color : Color.rgb( 204, 204, 204 )
    }
];

var mx : Number = 40;

var timer : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames :
        KeyFrame {
            time : 16ms
            action : function() {
                for( box in boxes ) {
                    box.update( mx );
                }
            }
        }
};

Frame {
    content : Canvas {
        background : Color.BLACK
        content : [
            Rectangle {
                width : 200, height : 200
                fill : Color.BLACK
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    mx = e.getX() * 0.4 - 200 / 5.0;
                }                
            },
            boxes
        ]
    }

    visible : true
    title : "Distance 1D"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timer.start();

class Box extends CustomNode {
    
    public attribute thickness : Number;
    public attribute ypos : Number;
    public attribute xpos : Number;
    public attribute div : Number;
    public attribute direction : Number;
    public attribute color : Color;
    
    public function update( mx : Number ): Void {
        xpos += direction * mx / div;
        
        if( xpos > 200 ) { xpos = -thickness; }
        if( xpos < -thickness ) { xpos = 200; }
    }
    
    public function create(): Node {
        return Rectangle {
            x : bind xpos, y : bind ypos,
            width : bind thickness, height : 100
            fill : bind color
        };
    }
}

