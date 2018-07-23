/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
