package input;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

var miliseconds : Integer;
var colors : Color[];

var timeline : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY
    keyFrames : 
        KeyFrame {
            keyTime : 16ms
            action : function() : Void {
                var milliseconds : Number = ( java.lang.System.currentTimeMillis() % 1000 ) as Integer;
                for( i in [0..9] ) {
                    var ii : Integer = i + 1;
                    var color : Number = ( milliseconds % ( ii * 10 )) / ( 10 * ii );
                    java.lang.System.out.println( "{color}" );
                    colors[i] = Color { 
                        red : color, green : color, blue : color };
                }
            }
        }
};

var barrs : Rect[];
for( i in [0..9] ) {
    var ii : Integer = i;
    insert Color {} into colors;
    insert Rect {
        x : i * 20, y : 0, width : 20, height : 200
        fill : bind colors[ii]
    } into barrs;
}

Frame {
    content : Canvas {
        content : bind barrs;
    };
    
    visible : true
    title : "Milliseconds"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
