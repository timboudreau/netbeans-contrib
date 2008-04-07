package input;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.System;

        
Frame {
    var input : StoringInput = StoringInput {};
    content : Canvas {
        content : bind input
        background : Color.GRAY    

        onMouseMoved : function( e : MouseEvent ): Void {
            input.mouseX = e.x;
            input.mouseY = e.y;
        }
    }
    
    visible : true
    title : "Storing Input"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

class StoringInput extends CompositeNode {

    attribute circles : Circle[];
    attribute mouseX : Number;
    attribute mouseY : Number;
    
    attribute length : Integer = 60;
    
    attribute timer : Timeline = Timeline {
        repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
        keyFrames :
            KeyFrame {
                keyTime : 16ms
                action : function() {
                    update();
                }
            }
    }
    
    public function update() : Void {
        for( i in [0..length - 1] ) {
            circles[i].cx = circles[i+1].cx;
            circles[i].cy = circles[i+1].cy;
            circles[i].radius = circles[i+1].radius;
        }
        circles[length-1] = Circle {
            cx : mouseX, cy : mouseY, radius : 30, fill : Color.WHITE, opacity : 0.3 
        };
        for( i in [0..length] ) {
            circles[i].radius = i / 4;
        }
    }
    
    public function composeNode(): Node {
        return Group {
            content : bind circles   
        };
    }
    
    init {
        for( i in [0..length] ) {
            insert Circle { cx : i * 10, cy : 100, fill : Color.WHITE } into circles;
        }   
        timer.start();
    }        
}