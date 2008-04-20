package transform;

import javafx.gui.*;
import javafx.animation.*;

var xPos : Number = -40;
   
var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : [
        KeyFrame {
            time : 0s                    
            values :  {
                xPos => -40.0
            }
        },
        KeyFrame {
            time : 5s                    
            values : {
                xPos => 200 + 40 tween Interpolator.LINEAR
            }
        },
    ]
};

Frame {
    content : Canvas {
        background : Color.GRAY
        content : [
            Rectangle {
                transform : [ javafx.gui.Translate { x : bind xPos, y : 60 }]
                width : 40, height : 40
                fill : Color.WHITE
            },
            Rectangle {
                transform : [ javafx.gui.Translate { x : bind 2 * xPos, y : 100 }]
                width : 40, height : 40
                fill : Color.BLACK
            }
        ]
    }
    
    visible : true
    title : "Translate"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
    
}

timeline.start();
