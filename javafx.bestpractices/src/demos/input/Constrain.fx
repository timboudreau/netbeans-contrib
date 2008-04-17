package input;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;


var esize : Number = 25;
var mouseX : Number = 100;
var mouseY : Number = 100;

var mx : Number = 100;
var my : Number = 100;

var easing : Number = 0.05;

var timer : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames :
        KeyFrame {
            time : 16ms
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
        content : [
            Rectangle {
                width : 200, height : 200
                fill : Color.BLACK
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    mouseX = e.getX();
                    if( mouseX < 100 - esize ) { mouseX = 100 - esize };
                    if( mouseX > 100 + esize ) { mouseX = 100 + esize };

                    mouseY = e.getY();
                    if( mouseY < 100 - esize ) { mouseY = 100 - esize };
                    if( mouseY > 100 + esize ) { mouseY = 100 + esize };
                }
            },
            Rectangle {
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
    }
    
    visible : true
    title : "Constrain"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timer.start();
