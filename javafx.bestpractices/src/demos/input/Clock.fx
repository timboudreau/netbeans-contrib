package input;

import javafx.ui.*;
import javafx.ui.canvas.*;
import java.lang.System;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.lang.Math;
import java.lang.System;
import java.util.Calendar;

var clockWork : ClockWork = ClockWork {};

Frame {
    content : Canvas {
        content : clockWork
        }
    
    visible : true
    title : "Clock"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }    
    }

clockWork.timer.start();

public class ClockWork extends CompositeNode {
    
    attribute seconds : Number;
    attribute minutes : Number;
    attribute hours : Number;
    
    attribute timerListener : ActionListener = ActionListener {
        public function actionPerformed( e : ActionEvent ): Void {
            update();
            }
        }
    
    public attribute timer : Timer = new Timer( 1000, timerListener );
    
    public function update(): Void {
        var calendar : Calendar = Calendar.getInstance();
        seconds = calendar.get( Calendar.SECOND );
        minutes = calendar.get( Calendar.MINUTE );
        hours = calendar.get( Calendar.HOUR_OF_DAY );        
    }
    
    public function composeNode(): Node {
        return Group {
            content : [
                Circle {
                    cx : 100
                    cy : 100
                    radius : 80
                    fill : Color.GRAY
                },
                Line {
                    transform : [ Rotate { angle : bind seconds * 6, cx : 100, cy : 100 }]
                    x1 : 100
                    y1 : 30
                    x2 : 100
                    y2 : 100
                    stroke : Color.WHITE
                },
                Line {
                    transform : [ Rotate { angle : bind minutes * 6, cx : 100, cy : 100 }]
                    x1 : 100
                    y1 : 40
                    x2 : 100
                    y2 : 100
                    stroke : Color.WHITE
                    strokeWidth : 2
                },
                Line {
                    transform : [ Rotate { angle : bind hours * 30, cx : 100, cy : 100 }]
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