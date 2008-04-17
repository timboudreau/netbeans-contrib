package input;

import javafx.gui.*;

var gx : Number = 10;
var gy : Number = 90;
var width : Number = 200;

var leftColor:Color = Color { red : 0.0, green : 0.4, blue : 0.6 };
var rightColor:Color = Color { red : 0.0, green : 0.2, blue : 0.4 };
        
Frame {
    content : Canvas {
        content : [
            Rectangle {
                width : 200, height : 200
                fill : Color.BLACK
                
                onMouseMoved : function( e : MouseEvent ): Void {
                    var l = -0.002 * ( e.getX() - 100 ) / 2 + 0.06;
                    var r = 0.002 * ( e.getX() - 100 ) / 2 + 0.06;

                    leftColor = Color { red : 0.0, green : l + 0.4, blue : l + 0.6 };
                    rightColor = Color { red : 0.0, green : r + 0.4, blue : r + 0.6 };

                    gx = e.getX() / 2;
                    gy = 100 - e.getX() / 2;

                    if( gx < 10 ) {
                    gx = 10;
                    } else if ( gx > 90 ) {
                    gx = 90;
                    }

                    if( gy > 90 ) {
                    gy = 90;
                    } else if( gy < 10 ) {
                    gy = 10;
                    }                
                }
            },
            Rectangle {
                x : bind width / 4 - gx
                y : bind width / 2 - gx
                width : bind gx * 2
                height : bind gx * 2
                fill : bind leftColor
            },
            Rectangle {
                x : bind width / 1.33 - gy
                y : bind width / 2 - gy
                width : bind gy * 2
                height : bind gy * 2
                fill : bind rightColor
            }           
        ]
    };
    
    visible : true
    title : "Mouse 1D"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}