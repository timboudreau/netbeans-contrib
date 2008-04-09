package input;

import javafx.ui.*;
import javafx.ui.canvas.*;

var mouseX : Number = 100;
var mouseY : Number = 100;
    
var buttons : Rect[] = [
    Rect { x : 5, y : 5, width : 10, height : 20, fill : Color.WHITE },
    Rect { x : 20, y : 5, width : 10, height : 20, fill : Color.WHITE },
    Rect { x : 35, y : 5, width : 10, height : 20, fill : Color.WHITE }
];

Frame {
    content : Canvas {
        background : Color.BLACK
        content : [
            Group {
                transform : Translate { x : bind mouseX - 32/2, y : bind mouseY - 25 }
                content : [
                    Rect { width : 50, height : 70, fill : Color.LIGHTGREY },
                    buttons

                ]
            }
        ]            

        onMousePressed: function( e : MouseEvent ): Void {
            if( e.button - 1 < 3 ) { buttons[e.button.intValue() - 1].fill = Color.BLACK }
        }

        onMouseReleased: function( e : MouseEvent ): Void {
            if( e.button - 1 < 3 ) { buttons[e.button.intValue() - 1].fill = Color.WHITE }
        }
        onMouseMoved: function( e : MouseEvent ): Void {
            mouseX = e.x;
            mouseY = e.y;
        }
    }
    
    visible : true
    title : "Mouse Press"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}