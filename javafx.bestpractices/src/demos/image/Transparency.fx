package image;

import javafx.ui.*;
import javafx.ui.canvas.*;

var x : Number = 40;
    
Frame {
    content : Canvas {
        content : [
            ImageView {
                image : Image { url : "resources/house.png" };
            },
            ImageView {
                image : Image { url : "resources/courage.png" };
                transform : [ Translate { x : bind x, y : 120 }, Scale { x : 0.3, y : 0.3 } ]
                opacity : 0.5
            }
        ]
        onMouseMoved : function( e : MouseEvent ):Void {
            x = e.x;
        }
    }
}