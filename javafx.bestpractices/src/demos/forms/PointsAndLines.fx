package forms;

import javafx.ui.*;
import javafx.ui.canvas.*;

var d = 40;
var p1 = d;
var p2 = p1 + d;
var p3 = p2 + d;
var p4 = p3 + d;
        
Frame {
    content : Canvas {
        width : 200
        height : 200
        background : Color.BLACK
        content : [
        Line {
            x1 : p3
            y1 : p3
            x2 : p2
            y2 : p3
            stroke : Color.LIGHTGREY
        },
        Line {
            x1 : p2
            y1 : p3
            x2 : p2
            y2 : p2
            stroke : Color.LIGHTGREY
        },
        Line {
            x1 : p2
            y1 : p2
            x2 : p3
            y2 : p2        
            stroke : Color.LIGHTGREY
        },
        Line {
            x1 : p3
            y1 : p2
            x2 : p3
            y2 : p3        
            stroke : Color.LIGHTGREY
        },
        // Points
        Line {
            x1 : p1
            y1 : p1
            x2 : p1
            y2 : p1
            stroke : Color.WHITE
        },
        Line {
            x1 : p1
            y1 : p3
            x2 : p1
            y2 : p3
            stroke : Color.WHITE
        },
        Line {
            x1 : p2
            y1 : p4
            x2 : p2
            y2 : p4
            stroke : Color.WHITE
        },
        Line {
            x1 : p3
            y1 : p1
            x2 : p3
            y2 : p1
            stroke : Color.WHITE
        },
        Line {
            x1 : p4
            y1 : p2
            x2 : p4
            y2 : p2
            stroke : Color.WHITE
        },
        Line {
            x1 : p4
            y1 : p4
            x2 : p4
            y2 : p4
            stroke : Color.WHITE
        }
        ]
    };
    
    visible : true
    title : "Points And Lines"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}