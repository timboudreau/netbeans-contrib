package transform;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;

var a : Number = 0.0;
var s : Number = bind Math.sin( a ) * 2;

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : [
        KeyFrame {
            time : 0s                    
            values : {
                a => 0.0 tween Interpolator.LINEAR
            }
        },
        KeyFrame {
            time : 5s                    
            values : {
                a => Math.PI tween Interpolator.LINEAR
            }
        }
    ]
};

Frame {
    content : Canvas {
        background : Color.GRAY
        content : [
            Rectangle {
                transform : [ 
                    javafx.gui.Translate { x : bind 100 - 40 * s / 2, y : bind 100 - 40 * s / 2 },
                    javafx.gui.Scale { x : bind s, y : bind s }
                ]
                x : 0, y : 0
                width : 40, height : 40
                fill : Color.BLACK
            },    
        ]
    }
    
    visible : true
    title : "Scale"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
