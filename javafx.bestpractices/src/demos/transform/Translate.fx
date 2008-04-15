package transform;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import com.sun.javafx.runtime.PointerFactory;
import com.sun.javafx.runtime.Pointer;

var xPos : Number = -40;
var pf : PointerFactory = PointerFactory {};
var bxPos = bind pf.make( xPos );
var pxPos = bxPos.unwrap();
   
var timeline : Timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY
    keyFrames : [
        KeyFrame {
            keyTime : 0s                    
            keyValues : 
                NumberValue {
                    target: pxPos;
                    value: -40.0
                }
        },
        KeyFrame {
            keyTime : 5s                    
            keyValues : 
                NumberValue {
                    target: pxPos;
                    value: 200 + 40
                    interpolate: NumberValue.LINEAR
                }
        },
    ]
};

Frame {
    content : Canvas {
        background : Color.GRAY
        content : [
            Rect {
                transform : [ javafx.ui.canvas.Translate { x : bind xPos, y : 60 }]
                width : 40, height : 40
                fill : Color.WHITE
            },
            Rect {
                transform : [ javafx.ui.canvas.Translate { x : bind 2 * xPos, y : 100 }]
                width : 40, height : 40
                fill : Color.BLACK
            }
        ]
    }
    
    visible : true
    title : "Translate"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
    
}

timeline.start();
