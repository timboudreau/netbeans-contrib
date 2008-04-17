package particles;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
import java.lang.System;

var parts : Particle[];

var timeline : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : 
        KeyFrame {
            time : 16.6ms
            action: function() {
                update();
            }                
    }
};

function update() : Void {
    insert Particle {
       x : 100
       y : 100
       vx : 1 - 2 * Math.random()
       vy : -2 * Math.random()
       accx : 0
       accy : 0.05
       timer : 100
    } into parts;
    var i = sizeof parts - 1;
    while( i >= 0 ) {
       parts[i.intValue()].update();
       if (parts[i.intValue()].isdead()) {
           delete parts[i.intValue()];
       }
       i--;
    }
}    
    
Frame {
    content : Canvas {
        background : Color.BLACK
        content : bind parts 
    }
    
    visible : true
    title : "Simple Particle System"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timeline.start();

public class Particle extends CustomNode {
    attribute x : Number;
    attribute y : Number;
    attribute vx : Number;
    attribute vy : Number;
    attribute accx : Number;
    attribute accy : Number;
    attribute timer : Number;
    
    function create(): Node {
       return Circle {
           centerX: bind x
           centerY: bind y
           radius: 5
           fill: Color.WHITE
           opacity: bind timer / 100
       };
    }
 
    function update(): Void {
       timer -= 1;
       x += vx;
       y += vy;
       vx += accx;
       vy += accy;
    }
    
    function isdead(): Boolean {
       return timer <= 0;
    }    
}
