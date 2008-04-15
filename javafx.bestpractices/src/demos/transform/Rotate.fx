package transform;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;
import java.util.Random;

var angle : Number = 0.0;
var jitter : Number = 0.0;

var random : Random = new Random();

var ticker : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
    keyFrames : 
        KeyFrame {
            keyTime : 20ms
            action : function(): Void {
                angle += jitter;
            }
    }
};

var jitterTimeline : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
    keyFrames :
        KeyFrame {
            keyTime : 1s
            action : function(): Void {
                jitter = random.nextDouble() * 12 - 6;
            }
    }
};
    
Frame {    
    content : Canvas {
        background : Color.GRAY
        content : Rect {
            transform : [ 
                javafx.ui.canvas.Rotate { angle : bind angle, cx : 100, cy : 100 }, 
                javafx.ui.canvas.Translate { x : 43, y : 43 } 
            ]
            width : 114, height : 114
            fill : Color.WHITE
        }
    }
    
    visible : true
    title : "Rotate"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

ticker.start();
jitterTimeline.start();
