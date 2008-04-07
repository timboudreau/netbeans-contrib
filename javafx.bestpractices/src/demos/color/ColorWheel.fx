package color;

import javafx.ui.*;
import javafx.ui.canvas.*;

public class ColorWheel extends CompositeNode {
    attribute segments : Number = 12;
    attribute steps : Number = 6;
    attribute radius : Number = 95;
    attribute valueShift : Number = 0;
    attribute stripes : Arc[];
    
    function composeNode() : Node {
        return Group {
            content : stripes
        };
    }
    
    init {
        var r = radius;
        var rStep = radius / steps;
        var colors : Color[];
        for( i in [1..segments + 1] ) {
            insert Color.rgb( 255, 255, 0 ) into colors;
        }
        for( i in [0..steps-1] ) {
            if( valueShift == 0 ) {
                colors[1] = Color.rgb( 255 - ( 255 / steps * i ), 255 - ( 255 / steps * i ), 0 );
                colors[2] = Color.rgb( 255 - ( 255 / steps ) * i, ( 255 / 1.5 ) - (( 255 / 1.5 ) / steps ) * i, 0 ); 
                colors[3] = Color.rgb( 255 - ( 255 / steps ) * i, ( 255 / 2 ) - ( ( 255 / 2 ) / steps ) * i, 0 ); 
                colors[4] = Color.rgb( 255 - ( 255 / steps ) * i, ( 255 / 2.5 ) - (( 255 / 2.5 ) / steps ) * i, 0 ); 
                colors[5] = Color.rgb( 255 - ( 255 / steps ) * i, 0, 0 );
                colors[6] = Color.rgb( 255 - ( 255 / steps ) * i, 0, ( 255 / 2 ) - (( 255 / 2 ) / steps ) * i );
                colors[7] = Color.rgb( 255 - ( 255 / steps ) * i, 0, 255 - ( 255 / steps ) * i ); 
                colors[8] = Color.rgb(( 255 / 2 ) - (( 255 / 2 ) / steps ) * i, 0, 255 - ( 255 / steps ) * i ); 
                colors[9] = Color.rgb( 0, 0, 255 - ( 255 / steps ) * i );
                colors[10] = Color.rgb( 0, 255 - ( 255 / steps ) * i, ( 255 / 2.5 ) - (( 255 / 2.5 ) / steps ) * i ); 
                colors[11] = Color.rgb( 0 , 255 - ( 255 / steps ) * i, 0 ); 
                colors[12] = Color.rgb(( 255 / 2 ) -(( 255 / 2 ) / steps ) * i, 255 - ( 255 / steps ) * i, 0 );
            } else if( valueShift == 1 ) {
                colors[1] = Color.rgb(( 255 / steps ) * i, ( 255 / steps ) * i, 0 ); 
                colors[2] = Color.rgb(( 255 / steps ) * i, (( 255 / 1.5 ) / steps ) * i, 0 ); 
                colors[3] = Color.rgb(( 255 / steps ) * i, (( 255 / 2 ) / steps ) * i, 0 ); 
                colors[4] = Color.rgb(( 255 / steps ) * i, (( 255 / 2.5 ) / steps ) * i, 0 ); 
                colors[5] = Color.rgb(( 255 / steps ) * i, 0, 0 ); 
                colors[6] = Color.rgb(( 255 / steps ) * i, 0, (( 255 / 2 ) / steps ) * i ); 
                colors[7] = Color.rgb(( 255 / steps ) * i, 0, ( 255 / steps ) * i ); 
                colors[8] = Color.rgb((( 255 / 2 ) / steps ) * i, 0, ( 255 / steps ) * i ); 
                colors[9] = Color.rgb( 0, 0, ( 255 / steps ) * i );
                colors[10] = Color.rgb( 0, ( 255 / steps ) * i, (( 255 / 2.5 ) / steps ) * i ); 
                colors[11] = Color.rgb( 0, ( 255 / steps ) * i, 0 ); 
                colors[12] = Color.rgb((( 255 / 2 ) / steps ) * i, ( 255 / steps ) * i, 0 );        
            }
            for( j in [1..segments] ) {
                var c = colors[j.intValue()];                
                insert Arc {
                    x : 100 - r, y : 100 - r
                    width : r * 2 , height : r * 2
                    startAngle : 360 / segments * ( segments - j - 0.5)
                    length : 360 / segments
                    fill : c //new Color( j / segments, 1, 1, 1 )
                    closure: ArcClosure.PIE
                } into stripes;
            }
            r -= rStep;
        }
    }    
}


Frame {
    content : Canvas {
        background : Color.GRAY
        content : ColorWheel {}
    }
    
    visible : true
    title : "Color Wheel"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}
