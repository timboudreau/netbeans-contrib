package image;

import javafx.ui.*;
import javafx.ui.canvas.*;
import javafx.ui.animation.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.sun.javafx.runtime.PointerFactory;
import com.sun.javafx.runtime.Pointer;

var pf = PointerFactory {};
var y = 0.0;
var bpy = bind pf.make( y );
var py = bpy.unwrap();

var timeline = Timeline {
    repeatCount: java.lang.Double.POSITIVE_INFINITY // HACK            
    keyFrames : [
        KeyFrame {
            keyTime : 0s
            keyValues : 
                NumberValue {
                    target: py;
                    value: -20.0
                }
        },
        KeyFrame {
            keyTime : 3s
            keyValues :
                NumberValue {
                    target : py;
                    value : 200
                    interpolate: NumberValue.LINEAR
            }
        }
    ]
};
        
Frame {    
    content : Canvas {
        content : [
            ImageView {
                image : Image { url : "resources/background.png" }
            },
            Group {
                transform : Translate { y : bind y }
                content : [
                    Line {
                        x1 : 0, y1 : 20, x2 : 200, y2 : 0 
                        stroke : Color.RED
                    },
                    Line {
                        x1 : 0, y1 : 20, x2 : 200, y2 : 0 
                        stroke : Color.RED
                        transform : Translate { y : 10 }
                    }
                ]
            }
        ]
    }
    
    visible : true
    title : "Background Image"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
