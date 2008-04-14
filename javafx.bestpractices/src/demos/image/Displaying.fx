package image;

import javafx.ui.*;
import javafx.ui.canvas.*;

var width : Number = 200;
var height : Number = 200;

var image = Image { url : "resources/background.png" };

Frame {
    content : Canvas {
        content : [
            ImageView {
                image : image
            },
            ImageView {
                image : image
                transform : [
                    Translate { x : width / 2 },
                    Scale { x : 0.5, y : 0.5 }]
            }
        ]
    }
    
    visible : true
    title : "Displaying"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}