package color;

import javafx.gui.*;

public class ColorWheel extends CustomNode {
    attribute segments : Number = 12;
    attribute steps : Number = 6;
    attribute radius : Number = 95;
    attribute valueShift : Number = 0;
    attribute stripes : Arc[];
    
    function create() : Node {
        return Group {
            content : bind stripes
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
                    centerX : 100, centerY : 100
                    radiusX : r , radiusY : r
                    startAngle : 360 / segments * ( segments - j - 0.5)
                    length : 360 / segments
                    fill : c
                    type : ArcType.ROUND
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
    width : 209
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }
}
