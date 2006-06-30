/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
