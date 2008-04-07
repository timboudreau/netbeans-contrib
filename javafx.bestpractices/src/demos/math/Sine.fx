package math;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.lang.Math;

import com.sun.javafx.runtime.PointerFactory;
import com.sun.javafx.runtime.Pointer;

var pf : PointerFactory = PointerFactory {}

var diameter : Number = bind 45 * Math.sin( angle ) + 210;
var angle : Number = 0.0;
var bpangle = bind pf.make( angle );
var pangle = bpangle.unwrap();

var circles : Circle[];

var timeline : Timeline = Timeline {
    toggle: false
    repeatCount: java.lang.Double.POSITIVE_INFINITY        
    keyFrames : [
        KeyFrame {
            keyTime : 0s;
            keyValues : 
                NumberValue {
                    target : pangle;
                    value : 0.0
                }
        },
        KeyFrame {
            keyTime : 15.71s;
            keyValues :
                NumberValue {
                    target : pangle;
                    value : Math.PI * 2
                    interpolate: NumberValue.LINEAR
                }
        }
    ]
};

for( i in [0..4] ) {
    insert Circle {
        transform : [ Rotate{ angle : angle + 45, cx : 130, cy : 65 } ]
            fill : Color.BLACK
            radius : bind diameter / 2
    } into circles;
    angle += 360 / 5;
}

Frame {
    content : Canvas {
        background : Color.LIGHTGREY
        content : [
            Circle {
                cx : 130
                cy : 65
                radius : 8
                fill : Color.WHITE
            }, circles ]        
    };

    
    visible : true
    title : "Sine"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }        
}

timeline.start();
