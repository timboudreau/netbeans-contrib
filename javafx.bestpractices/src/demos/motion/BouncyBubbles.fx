package motion;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
import java.util.Random;

var spring : Number = 0.05;
var gravity : Number = 0.05;

var bubbles : Bubble[];

var width : Number = 200;
var height : Number = 200;

var timer : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames : 
        KeyFrame {
            time : 16ms
            action : function() : Void {
                for( bubble in bubbles ) {
                    bubble.collide( bubbles, spring, width, height );
                    bubble.move( gravity, width, height );
                }
            }
        }
};
var rnd : Random = new Random();
    
for( i in [1..12] ) {
    insert Bubble {
        x : rnd.nextInt( width ), y : rnd.nextInt( height ), radius : rnd.nextInt( 10 ) + 10
        color : Color.WHITE, opacity : 0.8
    } into bubbles;
}

Frame {
    content : Canvas {
        background : Color.GRAY
        content : bind bubbles
    };

    visible : true
    title : "Bouncy Bubbles"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timer.start();

public class Bubble extends CustomNode {
    
    public attribute x : Number;
    public attribute y : Number;
    public attribute radius : Number;
    public attribute color : Color = Color.WHITE;
    
    public attribute vx : Number;
    public attribute vy : Number;
    
    public function collide( bubbles : Bubble[], spring : Number, width : Number, height : Number ): Void {
        for( bubble in bubbles ) {
            var dx : Number = bubble.x - x;
            var dy : Number = bubble.y - y;
            
            var distance : Number = Math.sqrt( dx * dx + dy * dy );
            var minDist : Number = bubble.radius + radius;
            
            if( distance < minDist ) {
                var angle : Number = Math.atan2( dy, dx );
                var tx : Number = x + Math.cos( angle ) * minDist;
                var ty : Number = y + Math.sin( angle ) * minDist;
                
                var ax : Number = ( tx - bubble.x ) * spring;
                var ay : Number = ( ty - bubble.y ) * spring;
                
                vx -= ax;
                vy -= ay;
                
                bubble.vx += ax;
                bubble.vy += ay;
            }
        }
    }
    
    public function move( gravity : Number, width : Number, height : Number ): Void {
        vy += gravity;
        x += vx;
        y += vy;
        
        if( x + radius > 200 ) {
            x = width - radius;
            vx *= - 0.9;
        } else if( x - radius < 0 ) {
            x = radius;
            vx *= 0.9;
        }
        if( y + radius > 200 ) {
            y = height - radius;
            vy *= -0.9;
        } else if( y - radius < 0 ) {
            y = radius;
            vy *= -0.9;
        }
    }
    
    public function create(): Node {
        return Circle {
            centerX : bind x, centerY : bind y, radius : bind radius
            fill : bind color
        };
    }
}