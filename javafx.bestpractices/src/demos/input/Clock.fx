package input;

import javafx.gui.*;
import javafx.animation.*;

import java.lang.Math;
import java.lang.System;
import java.util.Calendar;
import java.lang.System;

var clockWork : ClockWork = ClockWork {};

Frame {
    content : Canvas {
        content : clockWork
        }
    
    visible : true
    title : "Clock"
    width : 200
    height : 232
    closeAction : function() { java.lang.System.exit( 0 ); }    
    }

clockWork.timer.start();

public class ClockWork extends CustomNode {
    
    attribute seconds : Number;
    attribute minutes : Number;
    attribute hours : Number;
    
    public attribute timer : Timeline = Timeline {
        repeatCount : Timeline.INDEFINITE
        keyFrames : 
            KeyFrame {
                time : 1s
                action : function() {
                    var calendar : Calendar = Calendar.getInstance();
                    seconds = calendar.get( Calendar.SECOND );
                    minutes = calendar.get( Calendar.MINUTE );
                    hours = calendar.get( Calendar.HOUR_OF_DAY );        
                }
            }
    };
    
    public function create(): Node {
        return Group {
            content : [
                Circle {
                    transform : [ Translate { y : 100 }]
                    centerX : 100
                    centerX : 100
                    radius : 80
                    fill : Color.GRAY
                },
                Line {
                    transform : [ Rotate { angle : bind seconds * 6, x : 100, y : 100 }]
                    x1 : 100
                    y1 : 30
                    x2 : 100
                    y2 : 100
                    stroke : Color.WHITE
                },
                Line {
                    transform : [ Rotate { angle : bind minutes * 6, x : 100, y : 100 }]
                    x1 : 100
                    y1 : 40
                    x2 : 100
                    y2 : 100
                    stroke : Color.WHITE
                    strokeWidth : 2
                },
                Line {
                    transform : [ Rotate { angle : bind hours * 30, x : 100, y : 100 }]
                    x1 : 100
                    y1 : 50
                    x2 : 100
                    y2 : 100
                    stroke : Color.WHITE
                    strokeWidth : 4
                }            
                ]
        };
    }
}