package transform;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
import java.util.Random;

var angle : Number = 0.0;
var jitter : Number = 0.0;

var random : Random = new Random();

var ticker : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : 
        KeyFrame {
            time : 20ms
            action : function(): Void {
                angle += jitter;
            }
    }
};

var jitterTimeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames :
        KeyFrame {
            time : 1s
            action : function(): Void {
                jitter = random.nextDouble() * 12 - 6;
            }
    }
};
    
Frame {    
    content : Canvas {
        background : Color.GRAY
        content : Rectangle {
            transform : [ 
                javafx.gui.Rotate { angle : bind angle, x : 100, y : 100 }, 
                javafx.gui.Translate { x : 43, y : 43 } 
            ]
            width : 114, height : 114
            fill : Color.WHITE
        }
    }
    
    visible : true
    title : "Rotate"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

ticker.start();
jitterTimeline.start();
