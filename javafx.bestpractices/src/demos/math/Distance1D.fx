package math;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

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
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
    keyFrames :
        KeyFrame {
            keyTime : 16ms
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
        content : boxes

        onMouseMoved : function( e : MouseEvent ): Void {
            mx = e.x * 0.4 - 200 / 5.0;
        }
    }

    visible : true
    title : "Distance 1D"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

timer.start();

public class Box extends CompositeNode {
    
    public attribute thickness : Number;
    public attribute ypos : Number;
    public attribute xpos : Number;
    public attribute div : Number;
    public attribute direction : Number = 1.0;
    public attribute color : Color;
    
    public function update( mx : Number ): Void {
        xpos += direction * mx / div;
        
        if( xpos > 200 ) { xpos = -thickness; }
        if( xpos < -thickness ) { xpos = 200; }
    }
    
    public function composeNode(): Node {
        return Rect {
            x : bind xpos, y : ypos,
            width : thickness, height : 100
            fill : color
        };
    }
}