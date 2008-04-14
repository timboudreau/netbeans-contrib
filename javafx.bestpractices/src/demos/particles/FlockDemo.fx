package particles;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;
import java.util.Random;

import java.lang.System;

var flock : Flock = Flock{};

Frame {
    content : Canvas {
        background : Color.LIGHTGREY
        content : flock
    };            
    
    visible : true
    title : "Flock Demo"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
    
}

flock.start();

public class Flock extends CompositeNode {
    
    attribute _N : Integer = 50 on replace {
        for( i in [0.._N] ) {
            insert Boid {
                loc : Vector3D{ x : 50, y : 50 }
                maxspeed : 2.0
                maxforce : 0.05
            } into boids;
        }
    }
    attribute boids : Boid[];
    
    attribute ticker : Timeline = Timeline {
        repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK
        keyFrames :
            KeyFrame {
                keyTime : 20ms
                action : function() {
                    update();
                }
            }
    };
    
    public function update(): Void {
        for( i in [0.._N] ) {
            boids[i].run( boids );
        }
        boids[0].run( boids );
    }
    
    public function start(): Void {
        ticker.start();
    }
    
    public function composeNode(): Node {
        return {
            Group {
                content : bind boids
            }
        }
    }
}

class Boid extends CompositeNode {
    public attribute loc : Vector3D;
    attribute vel : Vector3D;
    attribute acc : Vector3D;
    attribute r : Number;
    attribute maxforce : Number;
    attribute maxspeed : Number;
    
    attribute x : Number = bind loc.x;
    
    attribute width : Number = 200;
    attribute height : Number = 200;

    init {
        var random : Random = new Random();
        //loc = Vector3D { x : 50, y : 50, z : 0 };
        vel = Vector3D { x : random.nextDouble() * 2 - 1, y : random.nextDouble() * 2 - 1 };
        acc = Vector3D { x : 0, y : 0, z : 0 };
        r = 2.0;
        maxspeed = 2.0;
        maxforce = 0.05;
    }
   
    function run( boids : Boid[] ): Void {
        flock( boids );
        update();
        borders();
    }

    function flock( boids : Boid[] ): Void {
        var sep : Vector3D = separate( boids );
        var ali : Vector3D = align( boids );
        var coh : Vector3D = cohesion( boids );
        
        sep.mult( 2.0 );
        ali.mult( 2.0 );
        coh.mult( 2.0 );
        
        acc.add( sep );
        acc.add( ali );
        acc.add( coh );
    }
    
    function update(): Void {
        // Update velocity
        vel.add( acc );
        // Limit speed
        vel.limit( maxspeed );
        loc.add( vel );
        // Reset accelertion to 0 each cycle
        acc.x = 0; acc.y = 0; acc.z = 0;        
    }
   
    function borders(): Void {
        if( loc.x < -r ) loc.x = width + r;
        if( loc.y < -r ) loc.y = height + r;
        if( loc.x > width + r ) loc.x = -r;
        if( loc.y > height + r ) loc.y = -r;
    }
    
    function composeNode(): Node {
        return Group {
            transform : [ 
                javafx.ui.canvas.Translate { x : bind loc.x, y : bind loc.y },
                javafx.ui.canvas.Rotate { angle : bind Math.toDegrees( vel.heading2D()) + 90 }
            ]
            content : [
                Line {
                    x1 : 0, y1 : -3, x2 : -2, y2 : 3, stroke : Color.WHITE },
                Line { 
                    x1 : -2, y1 : 3, x2 : 2, y2 : 3, stroke : Color.WHITE },
                Line { 
                    x1 : 2, y1 : 3, x2 : 0, y2 : -3, stroke : Color.WHITE }
            ]
        }
    }
   
    function steer( target : Vector3D, slowdown ) {
        var steer : Vector3D;
        var desired : Vector3D = target.sub( target, loc );
        var d = desired.magnitude();
        if( d > 0 ) {
            desired.normalize();
            if( slowdown and d < 100.0 ) {
                desired.mult( maxspeed * ( d / 100.0 ));
            } else {
                desired.mult( maxspeed );
            }
            steer = target.sub( desired, vel );
            steer.limit( maxforce );
        } else {
            steer = Vector3D {};
        }
        return steer;
    }
   
    function separate( boids : Boid[] ): Vector3D {
        var desiredseparation = 25.0;
        var sum : Vector3D = Vector3D {};
        var count : Number = 0;
        
        for( i in [0..sizeof boids - 1] ) {
            var other : Boid = boids[i];
            var d : Number = loc.distance( loc, other.loc );
            if( d > 0 and d < desiredseparation ) {
                var diff: Vector3D = loc.sub( loc, other.loc );
                diff.normalize();
                diff.div( d );
                sum.add( diff );
                count++;
            }            
        }
        if( count > 0 ) {
            sum.div( count );
        }
        return sum;
    }
    
    function align( boids : Boid[] ): Vector3D {
        var neighbordist = 50.0;
        var sum : Vector3D = Vector3D {};
        var count = 0;
        
        for( i in [0..sizeof boids - 1 ] ) {
            var other : Boid = boids[i];
            var d = loc.distance( loc, other.loc );
            if( d > 0 and d < neighbordist ) {
                sum.add( other.vel );
                count++;
            }
        }
        if( count > 0 ) {
            sum.div( count );
            sum.limit( maxforce );
        }
        return sum;
    }    
    
    function cohesion( boids : Boid[] ): Vector3D {
        var neighbordist = 50.0;
        var sum : Vector3D = Vector3D {};
        var count = 0;
        
        for( i in [0..sizeof boids - 1] ) {
            var other : Boid = boids[i];
            var d = loc.distance( loc, other.loc );
            if( d > 0 and d < neighbordist ) {
                sum.add( other.loc );
                count++;
            }
        }
        if( count > 0 ) {
            sum.div( count );
            return steer( sum, false );
        }
        return sum;
    }    
}    

class Vector3D {
    
    public attribute x : Number = 0.0;
    public attribute y : Number = 0.0;
    public attribute z : Number = 0.0;
    
    function magnitude(): Number {
        return Math.sqrt( x*x + y*y + z*z );
    }
    
    function add( v : Vector3D ): Void {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    function sub( v : Vector3D ): Void {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    function mult( n : Number ): Void {
        x *= n;
        y *= n;
        z *= n;
    }

    function div( n : Number ): Void {
        x /= n;
        y /= n;
        z /= n;
    }
        
    function normalize(): Void {
        var m = magnitude();
        if( m > 0 ) {
           div( m );
        }
    }

    function limit( max : Number ): Void {
        if( magnitude() > max ) {
          normalize();
          mult( max );
        }
    }
   
    function sub( v1 : Vector3D, v2 : Vector3D ) {
        return Vector3D {
            x : v1.x - v2.x, y : v1.y - v2.y, z : v1.z - v2.z
        };
    }
   
    function distance( v1 : Vector3D, v2 : Vector3D ): Number {
        var dx = v1.x - v2.x;
        var dy = v1.y - v2.y;
        var dz = v1.z - v2.z;
        
        return Math.sqrt( dx*dx + dy*dy + dz*dz );
    }
    
    function heading2D(): Number {
        return - Math.atan2( -y, x );
    } 
}
