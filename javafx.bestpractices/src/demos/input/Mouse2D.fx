package input;

import javafx.ui.*;
import javafx.ui.canvas.*;

var width : Number = 200;
var height : Number = 200;
var mouseX : Number = 0;
var mouseY : Number = 0;
    
Frame {
    content : Canvas {
        background : Color { red : 0.2, green : 0.2, blue :0.2 }
        content : [
            Rect {
                x : bind mouseX - mouseY / 4 - 5
                y : bind height / 2 - mouseY / 4 - 5
                width : bind mouseY / 2 + 10
                height : bind mouseY / 2 + 10
                fill : Color.WHITE
                opacity : 0.8
            },
            Rect {
                x : bind width - mouseX - (( height - mouseY ) / 4 ) - 5
                y : bind height / 2 - (( height - mouseY ) / 4 ) - 5
                width : bind (( height - mouseY ) / 2 ) + 10
                height : bind (( height - mouseY ) / 2 ) + 10
                fill : Color.WHITE
                opacity : 0.8
            }
        ]

        onMouseMoved : function( e : MouseEvent ): Void {
            mouseX = e.x;
            mouseY = e.y;
        }
    }
    
    visible : true
    title : "Mouse 2D"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}