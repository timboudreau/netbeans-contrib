package image;

import javafx.gui.*;

var width : Number = 200;
var height : Number = 200;

var img = Image { url : "{__DIR__}/../resources/background.png" };
    
Frame {
    content : Canvas {
        content : [
            ImageView {
                image : img
            },
            ImageView {
                image : img
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
    closeAction : function() { java.lang.System.exit( 0 ); }
}