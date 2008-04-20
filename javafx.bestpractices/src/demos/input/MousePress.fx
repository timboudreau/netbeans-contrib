package input;

import javafx.gui.*;

var mouseX : Number = 100;
var mouseY : Number = 100;
    
var buttons : Rectangle[] = [
    Rectangle { x : 5, y : 5, width : 10, height : 20, fill : Color.WHITE },
    Rectangle { x : 20, y : 5, width : 10, height : 20, fill : Color.WHITE },
    Rectangle { x : 35, y : 5, width : 10, height : 20, fill : Color.WHITE }
];

Frame {
    content : Canvas {
        content : [
            Rectangle {
                width : 200, height : 200
                fill : Color.BLACK
                
                onMousePressed: function( e : MouseEvent ): Void {
                    if( e.getButton() - 1 < 3 ) { buttons[e.getButton() - 1].fill = Color.BLACK }
                }

                onMouseReleased: function( e : MouseEvent ): Void {
                    if( e.getButton() - 1 < 3 ) { buttons[e.getButton() - 1].fill = Color.WHITE }
                }
                onMouseMoved: function( e : MouseEvent ): Void {
                    mouseX = e.getX();
                    mouseY = e.getY();
                }                
            },
            Group {
                transform : Translate { x : bind mouseX - 32/2, y : bind mouseY - 25 }
                content : [
                    Rectangle { width : 50, height : 70, fill : Color.LIGHTGREY },
                    buttons

                ]
            }
        ]            
    }
    
    visible : true
    title : "Mouse Press"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}