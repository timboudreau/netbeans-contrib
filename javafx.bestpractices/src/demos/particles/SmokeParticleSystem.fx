package particles;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;
import java.util.Random;
import java.lang.System;
    
var parts : Particle[];
var random : Random = new Random();    
var acc : Number;

var timeline : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
    keyFrames : 
        KeyFrame {
            keyTime : 16.6ms
            action: function() {
                update();
            }                
    }
};

function update() : Void {
    insert Particle {
        x : 84
        y : 164
        vx : 0.3 * random.nextGaussian()
        vy : 0.3 * random.nextGaussian() - 1
        timer : 100
        acc : bind acc
    } into parts;
    var i = sizeof parts - 1;
    while( i >=0 ) {
        parts[i.intValue()].update();
        if( parts[i.intValue()].isdead()) {
            delete parts[i.intValue()];
        }
        i--;
    }
}

function composeNode() : Node {
    return Group {
       content : bind parts
    };
}
    
Frame {                    
    content : Canvas {
        background : Color.BLACK
        content : bind [
            parts,
            Line {
                x1 : bind 100 + ( 500 * acc )
                y1 : 50
                x2 : 100
                y2 : 50
                stroke : Color.WHITE
            },
            Line {
                x1 : bind 100 + ( 500 * acc )
                y1 : 50
                x2 : bind 100 + ( 500 * acc ) - 4 * acc / Math.abs( acc )
                y2 : 48
                stroke : Color.WHITE
            },
            Line {
                x1 : bind 100 + ( 500 * acc )
                y1 : 50
                x2 : bind 100 + ( 500 * acc ) - 4 * acc / Math.abs( acc )
                y2 : 52
                stroke : Color.WHITE
            }
        ]
        onMouseMoved : function( e : MouseEvent ): Void {
            acc = ( e.x - 100 ) / 1000;
        }

    }
    
    visible : true
    title : "Smoke Particle System"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}
timeline.start();

public class Particle extends CompositeNode {
    attribute x : Number;
    attribute y : Number;
    attribute vx : Number;
    attribute vy : Number;
    attribute timer : Number;
    attribute acc : Number;
    
    function composeNode(): Node {
        return ImageView {
            transform: [ Translate{ x : bind x, y : bind y } ]
            image : Image { url: "resources/texture.png" }
            opacity: bind timer / 100
        };
    }
 
    function update(): Void {
        timer -= 2.5;
        x += vx;
        y += vy;
        vx += acc;
    }
    
    function isdead(): Boolean {
       return timer <= 0;
    }    
}
