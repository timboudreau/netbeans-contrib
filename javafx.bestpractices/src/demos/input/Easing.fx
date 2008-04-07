package input;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;

var easing : Ball = Ball {};
    
Frame {
    content : Canvas {
        background : Color{ red: 0.2, green : 0.2, blue : 0.2 }

        content : easing

        onMouseMoved : function( e : MouseEvent ): Void {
            easing.targetX = e.x;
            easing.targetY = e.y;
        }
    };
    
    visible : true
    title : "Easing"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }    
}

class Ball extends CompositeNode {
    attribute x : Number;
    attribute y : Number;
    public attribute targetX : Number = 100;
    public attribute targetY : Number = 100;
    
    attribute easing : Number = 0.05;
    
    init {
        timer.start();
    }
    
    attribute timer : Timeline = Timeline {
        repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
        keyFrames :
            KeyFrame {
                keyTime : 20ms
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
    
    public function composeNode(): Node {
        return Circle {
            cx : bind x
            cy : bind y
            radius : 33 / 2 
            fill : Color.WHITE
        };
    }
}