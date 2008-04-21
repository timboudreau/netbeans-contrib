package particles;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
import java.util.Random;

Frame {                    
    content : Canvas {
        background : Color.BLACK
        content : CustomCanvas {}
    }

    visible : true
    title : "Smoke Particle System"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

public class CustomCanvas extends CustomNode {

    private attribute acc : Number;
    private attribute timeline : Timeline;
    private attribute parts : Particle[];
    private attribute random : Random;;    

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

    public function create(): Node {
        random = new Random();
        timeline = Timeline {
            repeatCount: Timeline.INDEFINITE
            keyFrames : 
                KeyFrame {
                    time : 16.6ms
                    action: function() {
                        update();
                    }                
                }
            };
        timeline.start();


        return Group {
            content : bind [
                Rectangle {
                    width : 200, height : 200
                    fill : Color.BLACK
                    blocksMouse : true

                    onMouseMoved : function( e : MouseEvent ): Void {
                        acc = ( e.getX() - 100 ) / 1000;
                    }
                },
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
                },
                parts
            ]
        };
    }
}

public class Particle extends CustomNode {
    attribute x : Number;
    attribute y : Number;
    attribute vx : Number;
    attribute vy : Number;
    attribute timer : Number;
    attribute acc : Number;
    
    function create(): Node {
        return ImageView {
            transform: [ Translate{ x : bind x, y : bind y } ]
            image : Image { url: "{__DIR__}/../resources/texture.png" }
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
