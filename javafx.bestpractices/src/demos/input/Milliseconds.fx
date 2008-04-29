package input;

import javafx.gui.*;
import javafx.animation.*;

var miliseconds : Integer;
var colors : Color[];

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : 
        KeyFrame {
            time : 16ms
            action : function() : Void {
                var milliseconds : Number = ( java.lang.System.currentTimeMillis()) as Number;                
                for( i in [0..9] ) {
                    var ii : Integer = ( i + 1 ) * 100;
                    var color : Number = milliseconds % ii / ii;
                    colors[i] = Color { 
                        red : color, green : color, blue : color };
                }
            }
        }
};

var barrs : Rectangle[];
for( i in [0..9] ) {
    var ii : Integer = i;
    insert Color {} into colors;
    insert Rectangle {
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
    width : 208
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
