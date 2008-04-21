package game;

import javafx.gui.*;
import javafx.animation.*;

var STICK : Integer = 1; 
var BOUNCING : Integer = 2;
var state : Integer = STICK;

var paddle : Paddle = Paddle {};
var ball : Ball = Ball { x : paddle.x, y : paddle.y - 5 };

var bouncer : Timeline = Timeline {
    repeatCount: Timeline.INDEFINITE
    keyFrames :
        KeyFrame {
            time : 16ms
            action : function() {
                // Bounce from walls
                if( ball.x + ball.vx < ball.radius ) { ball.vx = -ball.vx; }
                if( ball.x + ball.vx > 200 - ball.radius ) { ball.vx = -ball.vx; }

                if( ball.y + ball.vy < ball.radius ) { ball.vy = -ball.vy; }
                if( ball.y + ball.vy > 200 + ball.radius ) {
                    state = STICK;
                    bouncer.stop();
                }

                // Bouncle from paddle
                if( ball.x + ball.vx >= paddle.x - paddle.width / 2 and ball.x + ball.vx <= paddle.x + paddle.width / 2 ) {
                    if( ball.y + ball.vy + ball.radius >= paddle.y and ball.y + ball.vy - ball.radius <= paddle.y + paddle.height ) {
                        ball.vy = -ball.vy;
                    }
                } else if( ball.y + ball.vy >= paddle.y and ball.y + ball.vy <= paddle.y + paddle.height ) {
                    if( ball.x + ball.vx + ball.radius >= paddle.x - paddle.width / 2 and ball.x + ball.vx - ball.radius <= paddle.x + paddle.width / 2 ) {
                        ball.vx = -ball.vx;
                    }
                }

                // Bounce from brick
                var i : Integer = 0;
                for( brick in bricks ) {
                    if( ball.x + ball.vx >= brick.x and ball.x + ball.vx <= brick.x + brick.width ) {
                        if( ball.y + ball.vy + ball.radius >= brick.y and ball.y + ball.vy - ball.radius <= brick.y + brick.height ) {
                            ball.vy = -ball.vy;
                            delete bricks[i];
                        }
                    } else if( ball.y + ball.vy >= brick.y and ball.y + ball.vy <= brick.y + brick.height ) {
                        if( ball.x + ball.vx + ball.radius >= brick.x and ball.x + ball.vx - ball.radius <= brick.x + brick.width ) {
                            ball.vx = -ball.vx;
                            delete bricks[i];
                        }
                    }
                    i++;
                }
                ball.x += ball.vx;
                ball.y += ball.vy;
            }
        }
};

var bricks : Brick[];
// Create bricks
for( j in [0..5] ) {
    for( i in [0..9] ) {
        insert Brick {
            x : i * 20, y : j * 10 + 30
        } into bricks;
    }
}

Frame {
    content : Canvas {
        content : bind [
            Rectangle {
                width : 200, height : 200
                fill : Color.LIGHTGREY
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    if( e.getX() < paddle.width / 2 ) { paddle.x = paddle.width / 2; }
                    else if( e.getX() > 200 - paddle.width / 2 ) { paddle.x = 200 - paddle.width / 2; }
                    else { paddle.x = e.getX(); }
                    if( state == STICK ) {
                        ball.x = paddle.x;
                        ball.y = paddle.y - ball.radius;
                    }
                }
                onMousePressed : function( e : MouseEvent ): Void {
                    if( state == STICK ) {
                        state = BOUNCING;
                        bouncer.start();
                    }
                }
            },
            paddle, ball, bricks
        ]

    }
    
    visible : true
    title : "Bounce Game"
    width : 209
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}

class Paddle extends CustomNode {
    
    public attribute x : Number = 85;
    public attribute y : Number = 180;
    
    public attribute width : Number = 30;
    public attribute height : Number = 10;
    
    public function create(): Node {
        return Rectangle {
            transform : Translate { x : bind x - width / 2, y : bind y }
            width : bind width, height : bind height,
            fill : Color.WHITE
            stroke : Color.BLUE, strokeWidth : 1
        };
    }
}

class Brick extends CustomNode {
    public attribute x : Number;
    public attribute y : Number;
    public attribute width : Number = 20;
    public attribute height : Number = 10;
    
    public function create(): Node {
        return Rectangle {
            transform : Translate { x : bind x, y : bind y }
            width : bind width, height : bind height
            fill : Color.GRAY
            stroke : Color.WHITE, strokeWidth : 1
        };
    }
}

class Ball extends CustomNode {
    public attribute x : Number = 100;
    public attribute y : Number = 100;
    public attribute radius : Number = 5;

    public attribute vx : Number = 1.0;
    public attribute vy : Number = -1.0;
        
    public function create(): Node {
        return Circle {
            transform : Translate { x : bind x, y : bind y }
            radius : bind radius
            fill : Color.RED
        }
    }
}