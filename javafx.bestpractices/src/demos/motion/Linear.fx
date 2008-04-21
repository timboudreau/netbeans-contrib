package motion;

import javafx.gui.*;
import javafx.animation.*;


var y : Number = 200;

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : 
        KeyFrame {
            time : 16ms
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
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
