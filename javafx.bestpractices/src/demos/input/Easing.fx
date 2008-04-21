package input;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;

var easing : Ball = Ball {};
    
Frame {
    content : Canvas {
        content : [
            Rectangle {  
                width : 200, height : 200
                fill : Color{ red: 0.2, green : 0.2, blue : 0.2 }
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    easing.targetX = e.getX();
                    easing.targetY = e.getY();
                }
            },
            easing
        ]

    };
    
    visible : true
    title : "Easing"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }    
}

class Ball extends CustomNode {
    attribute x : Number;
    attribute y : Number;
    public attribute targetX : Number = 100;
    public attribute targetY : Number = 100;
    
    attribute easing : Number = 0.05;
    
    init {
        timer.start();
    }
    
    attribute timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames :
            KeyFrame {
                time : 20ms
                action : function() {
                    var dx = targetX - x;
                    if( Math.abs( dx ) > 1 ) {
                        x += dx * easing;
                    }

                    var dy = targetY - y;
                    if( Math.abs( dy ) > 1 ) {
                        y += dy * easing;
                }
            }
        }
    };
    
    public function create(): Node {
        return Circle {
            centerX : bind x
            centerY : bind y
            radius : 33 / 2 
            fill : Color.WHITE
        };
    }
}