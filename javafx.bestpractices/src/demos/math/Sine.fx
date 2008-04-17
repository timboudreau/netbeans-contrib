package math;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;

var diameter : Number = bind 45 * Math.sin( angle ) + 210;
var angle : Number = 0.0;

var circles : Circle[];

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE     
    keyFrames : [
        KeyFrame {
            time : 0s;
            values : {
                angle => 0.0
            }
        },
        KeyFrame {
            time : 10s;
            values : {
                angle => Math.PI * 2 tween Interpolator.LINEAR
            }
        }
    ]
};

for( i in [0..4] ) {
    insert Circle {
        transform : [ Rotate{ angle : angle + 45, x : 130, y : 65 } ]
            fill : Color.BLACK
            radius : bind diameter / 2
    } into circles;
    angle += 360 / 5;
}

Frame {
    content : Canvas {
        background : Color.LIGHTGREY
        content : [
            Circle {
                centerX : 130
                centerY : 65
                radius : 8
                fill : Color.WHITE
            }, circles ]        
    };

    
    visible : true
    title : "Sine"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }        
}

timeline.start();
