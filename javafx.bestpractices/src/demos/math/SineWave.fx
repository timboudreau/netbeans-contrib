package math;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
    
var dots : Circle[];
var theta : Number = 0.0;
var amplitude : Number = 75;
var xspacing : Number = 8;
var period : Number = 500;
var dx : Number = ( Math.PI * 2 / period ) * xspacing;
var time : Number = 0.0;

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE        
    keyFrames : 
        KeyFrame {
            time : 16.6ms
            action: function() {
                time += 0.02;
            }                
    }
};

var x = theta;
for( i in [0..25] ) {      
    var xx = x;
    insert Circle {
        centerX : i * 8
        centerY : bind 100 + Math.sin( xx + time ) * amplitude
        radius : 8
        fill : Color.WHITE
        opacity : 0.3
    } into dots;
    x += dx;
}

Frame {
    content : Canvas {
        background : Color.BLACK
        content : dots
    }
    
    visible : true
    title : "Sine Wave"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }        
}

timeline.start();
