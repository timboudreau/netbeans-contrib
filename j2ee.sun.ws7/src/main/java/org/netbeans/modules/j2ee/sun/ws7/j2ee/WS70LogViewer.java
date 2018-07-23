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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.*;
import java.io.*;

import org.openide.ErrorManager;
import org.openide.windows.InputOutput;
import org.openide.windows.*;
/** Connects the output stream of a file to the IDE output window.
 *
 * @author ludo Adapted by Mukesh Garg for Webserver70
 */
public class WS70LogViewer extends Thread {
    boolean shouldStop = false;
    FileInputStream  filestream=null;
    BufferedReader    ins;
    InputOutput io;
    File fileName;
    
    /** Connects a given process to the output window. Returns immediately, but threads are started that
     * copy streams of the process to/from the output window.
     * @param process process whose streams to connect to the output window
     * @param url deployment manager URL
     */
    public WS70LogViewer(final File fileName) {        
        this.fileName=fileName;        
    }
    
    
    public void run() {
        int MAX_LINES = 15000;
        int LINES = 2000;
        int OLD_LINES = 600;
        int lines;
        Ring ring = new Ring(OLD_LINES);
        int c;
        String line;
        
                                // Read the log file without
                                // displaying everything
        try {
            while ((line = ins.readLine()) != null) {
                ring.add(line);
            } // end of while ((line = ins.readLine()) != null)
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } // end of try-catch
        
                                // Now show the last OLD_LINES
        lines = ring.output();
        ring.setMaxCount(LINES);
        
        while (shouldStop ==false) {
            try {
                if (lines >= MAX_LINES) {
                    io.getOut().reset();
                    lines = ring.output();
                } // end of if (lines >= MAX_LINES)

                while ((line = ins.readLine()) != null) {
                    if ((line = ring.add(line)) != null) {
                        io.getOut().println(line);
                        lines++;
                    } // end of if ((line = ring.add(line)) != null)
                }
                
            }catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            try {
                sleep(10000);
                ////System.out.println("io close or not"+io.isClosed());
                if (io.isClosed()){//tab is closed by the user
                    shouldStop =true;
                }
                else{
                                // it is possilbe in the case of only
                                // 1 tab, that the tab is hidden, not
                                // closed. In this case we need to
                                // detect that and close our stream
                                // anyway to unlock the log file
                    shouldStop =true; //assume the tab is hidden
                    TopComponent.Registry rr= TopComponent.getRegistry();
                    Set ss = rr.getOpened();
                    Iterator ttt = ss.iterator();
                    while (ttt.hasNext()){
                        Object o = ttt.next();
                        String sss=""+o;
                        if (sss.startsWith("org.netbeans.core.output2.OutputWindow")){
                            // the tab is not hidden so we should not stopped!!!
                            shouldStop =false;
                        }
                    }
                }
            }catch (Exception e){
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            }
        }
        ///System.out.println("end of infinite loop for log viewer\n\n\n\n");
        stopUpdatingLogViewer();
        
    }
    /* display the log viewer dialog
     *
     **/
    
    public void showLogViewer(InputOutput inputoutput) throws IOException{
        shouldStop = false;
        io = inputoutput;
        io.getOut().reset();
        io.select();
        filestream = new FileInputStream(fileName);
        // RAVE ins = new BufferedReader(new InputStreamReader(filestream,"UTF-8"));//NOI18N
                                // Use the default charset!
        ins = new BufferedReader(new InputStreamReader(filestream));
        
        start();
    }
    
    /* stop to update  the log viewer dialog
     *
     **/
    
    public void stopUpdatingLogViewer()   {
        shouldStop = true;
        
        try{
            ins.close();
            filestream.close();
            io.closeInputOutput();
            io.setOutputVisible(false);
        }
        catch (IOException e){
            
        }
    }
    

    private class Ring {
        private int maxCount;
        private int count;
        private LinkedList anchor;
        
        public Ring(int max) {
            maxCount = max;
            count = 0;
            anchor = new LinkedList();
        }
    
        public String add(String line) {
            if (line == null || line.equals("")) { // NOI18N
                return null;
            } // end of if (line == null || line.equals(""))
            
            while (count >= maxCount) {
                anchor.removeFirst();
                count--;
            } // end of while (count >= maxCount)
            
            anchor.addLast(line);
            count++;
            
            return line;
        }

        public void setMaxCount(int newMax) {
            maxCount = newMax;
        }
        
        public int output() {
            int i = 0;
            Iterator it = anchor.iterator();
            
            while (it.hasNext()) {
                io.getOut().println((String)it.next());
                i++;
            } // end of while (it.hasNext())

            return i;
        }
        
        public void reset() {
            anchor = new LinkedList();
        }
    }
}

