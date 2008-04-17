package image;

import javafx.gui.*;

var x : Number = 40;
    
var img : Image = Image { url : "{__DIR__}/../resources/overlay.png" };

Frame {
    content : Canvas {
        content : [
            ImageView {
                image : Image { url : "{__DIR__}/../resources/background.png" }
                onMouseMoved : function( e : MouseEvent ):Void {
                    x = e.getX() - 100 * 0.5;
                }
            },
            ImageView {
                image : bind img
                transform : [ Translate { x : bind x, y : 100 - 32 }, Scale { x : 0.5, y : 0.5 } ]
                opacity : 0.5
            }
        ]
    }
    
    visible : true
    title : "Transparency"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}
