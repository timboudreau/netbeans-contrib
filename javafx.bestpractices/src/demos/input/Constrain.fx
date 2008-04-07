package input;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;


var esize : Number = 25;
var mouseX : Number = 100;
var mouseY : Number = 100;

var mx : Number = 100;
var my : Number = 100;

var easing : Number = 0.25;

var timer : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
    keyFrames :
        KeyFrame {
            keyTime : 1ms
            action : function() {
                if( Math.abs( mouseX - mx ) > 0.1 ) {
                    mx = mx + (mouseX - mx ) * easing;
                }
                if( Math.abs( mouseY - my ) > 0.1 ) {
                    my = my + ( mouseY - my ) * easing;
                }
            }
        }
};

Frame {
    content : Canvas {
        background : Color.BLACK
        content : [
            Rect {
                x : 50, y : 50
                width : 100, height : 100
                fill : Color.GRAY
            },
            Circle {
                transform : Translate { x : bind mx, y : bind my }
                radius : esize,
                fill : Color.WHITE
            }
        ]

        onMouseMoved : function( e : MouseEvent ): Void {
            mouseX = e.x;
            if( mouseX < 100 - esize ) { mouseX = 100 - esize };
            if( mouseX > 100 + esize ) { mouseX = 100 + esize };

            mouseY = e.y;
            if( mouseY < 100 - esize ) { mouseY = 100 - esize };
            if( mouseY > 100 + esize ) { mouseY = 100 + esize };
        }
    }
    
    visible : true
    title : "Constrain"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

timer.start();
