package motion;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;


var y : Number = 200;

var timeline : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY
    keyFrames : 
        KeyFrame {
            keyTime : 16ms
            action : function() : Void {
                y -= 1;
                if( y == -1 ) { y = 200 }
            }
        }
};
    
Frame {
    content : Canvas {
        background : Color.DARKGRAY
        content : Line {
            transform : [ Translate { y : bind y }]
            x2 : 200
            stroke : Color.WHITE
        }
    }
    
    visible : true
    title : "Linear"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
