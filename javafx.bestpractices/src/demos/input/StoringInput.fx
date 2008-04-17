package input;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.System;

        
Frame {
    var input : StoringInput = StoringInput {};
    content : Canvas {
        content : bind [
            Rectangle {
                width : 200, height : 200
                fill : Color.GRAY
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    input.mouseX = e.getX();
                    input.mouseY = e.getY();
                }                
            },
            input
        ]
    }
    
    visible : true
    title : "Storing Input"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

class StoringInput extends CustomNode {

    attribute circles : Circle[];
    attribute mouseX : Number;
    attribute mouseY : Number;
    
    attribute length : Integer = 60;
    
    attribute timer : Timeline = Timeline {
        repeatCount: Timeline.INDEFINITE
        keyFrames :
            KeyFrame {
                time : 16ms
                action : function() {
                    update();
                }
            }
    }
    
    public function update() : Void {
        for( i in [0..length - 1] ) {
            circles[i].centerX = circles[i+1].centerX;
            circles[i].centerY = circles[i+1].centerY;
            circles[i].radius = circles[i+1].radius;
        }
        circles[length] = Circle {
            centerX : mouseX, centerY : mouseY, radius : 30, fill : Color.WHITE, opacity : 0.3 
        };
        for( i in [0..length] ) {
            circles[i].radius = i / 4;
        }
    }
    
    public function create(): Node {
        return Group {
            content : bind circles   
        };
    }
    
    init {
        for( i in [0..length] ) {
            insert Circle { fill : Color.WHITE } into circles;
        }   
        timer.start();
    }        
}