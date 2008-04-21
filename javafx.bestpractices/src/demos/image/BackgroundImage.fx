package image;

import javafx.gui.*;
import javafx.animation.*;

var y : Number;

var timeline = Timeline {
    repeatCount: Timeline.INDEFINITE            
    keyFrames : [
        KeyFrame {
            time : 0s
            values : 
                y => -20
        },
        KeyFrame {
            time : 3s
            values :
                y => 200 tween Interpolator.LINEAR
        }
    ]
};
        
Frame {    
    content : Canvas {
        content : [
            ImageView {
                image : Image { url : "{__DIR__}/../resources/background.png" }
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
    closeAction : function() { java.lang.System.exit( 0 ); }
}

timeline.start();
