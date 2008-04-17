package forms;

import javafx.gui.*;

Frame {
    content : Canvas {    
        background : Color.BLACK
        content : [
        Polygon {
            points : [ 10, 10, 10, 200, 45, 200 ]
            fill : Color.LIGHTGREY
        },
        Rectangle {
            x : 45
            y : 34
            width : 35
            height : 35
            fill : Color.LIGHTGREY
        },
        Polygon {
            points : [ 105, 10, 120, 10, 120, 200, 80, 200 ]
            fill : Color.LIGHTGREY
        },
        Circle {
            centerX : 140
            centerY : 80
            radius : 20
            fill : Color.LIGHTGREY
        },
        Polygon {
            points : [ 160, 10, 195, 200, 160, 200 ]
            fill : Color.LIGHTGREY
        }
        ]
    };
    
    visible : true
    title : "Shape Primitives"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}