/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.PrintStream;
import javax.swing.JFrame;

/** Class which is used for automatic Exception Dialog closing
 */
public class ExceptionDialogCloser extends Thread {
    
    boolean running = false;
    boolean stopped = false;
    
    PrintStream out = null;
    
/** Creates and start ExceptionDialogCloser
 */    
    public ExceptionDialogCloser () {
        start ();
    }
    
    public void setOut (PrintStream _out) {
        out = _out;
    }
    
/** This method provides whole closing process
 */    
    public void run () {
        while (!stopped) {
            try {
                if (!running) {
                    synchronized (this) {
                        if (!stopped)
                            wait ();
                    }
                    if (stopped)
                        break;
                }
                sleep (2000);
            } catch (Exception e) {}
            Frame[] f = JFrame.getFrames();
            for (int a = 0; a < f.length; a ++) {
                Window[] aw = f[a].getOwnedWindows ();
                for (int w = 0; w < aw.length; w ++) {
                    if (aw[w] instanceof Dialog  &&  "Exception".equals (((Dialog) aw[w]).getTitle ())  &&  aw[w].isShowing ()) {
                        if (out != null)
                            out.println("Closing: " + aw[w]);
                        aw[w].dispose ();
                    }
                }
            }
        }
    }
    
/** Stop closing process
 */    
    public void cancel () {
        running = false;
        synchronized (this) {
            stopped = true;
            notifyAll ();
        }
    }
    
/** Pause closing process
 */    
    public void pause () {
        running = false;
    }
    
/** Rerun closing process
 */    
    public void play () {
        running = true;
        synchronized (this) {
            notifyAll ();
        }
    }
    
}
