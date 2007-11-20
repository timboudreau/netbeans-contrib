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
package org.netbeans.modules.j2ee.oc4j.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;

/**
 * This class is capable of tailing the specified file or input stream. It
 * checks for changes at the specified intervals and outputs the changes to
 * the given I/O panel in NetBeans
 *
 * @author  Michal Mocnak
 */
public class OC4JLogger {
    
    private static final Logger LOGGER = Logger.getLogger(OC4JLogger.class.getName());
    
    /**
     * Amount of time in milliseconds to wait between checks of the input
     * stream
     */
    private static final int delay = 1000;
    
    /**
     * Singleton model pattern
     */
    private static Map<String, OC4JLogger> instances = new HashMap<String, OC4JLogger>();
    
    /**
     * The I/O window where to output the changes
     */
    private InputOutput io;
    
    /**
     * Creates and starts a new instance of OC4JLogger
     *
     * @param uri the uri of the server
     */
    private OC4JLogger(String uri) {
        io = UISupport.getServerIO(uri);
        
        if (io == null) {
            return; // finish, it looks like this server instance has been unregistered
        }
        
        // clear the old output
        try {
            io.getOut().reset();
        } catch (IOException ioe) {
            // no op
        }
        
        io.select();
    }
    
    /**
     * Returns uri specific instance of OC4JLogger
     *
     * @param uri the uri of the server
     * @return uri specific instamce of OC4JLogger
     */
    public static OC4JLogger getInstance(String uri) {
        if (!instances.containsKey(uri))
            instances.put(uri, new OC4JLogger(uri));
        
        return instances.get(uri);
    }
    
    /**
     * Reads a newly included InputSreams
     *
     * @param inputStreams InputStreams to read
     */
    public void readInputStreams(InputStream[] inputStreams) {
        for(InputStream inputStream : inputStreams)
            RequestProcessor.getDefault().post(new OC4JLoggerRunnable(inputStream));
    }
    
    /**     
     * Reads a newly included Files
     * 
     * @param files Files to read
     */
    public void readFiles(File[] files) {
        for(InputStream inputStream : getInputStreamsFromFiles(files))
            RequestProcessor.getDefault().post(new OC4JLoggerRunnable(inputStream));
    }
    
    /**
     * Writes a message into output
     * 
     * @param s message to write
     */
    public synchronized void write(String s) {
        io.getOut().println(s);
    }
    
    /**
     * Selects output panel
     */
    public synchronized void selectIO() {
        io.select();
    }

    // TODO fix this ugly design
    private static InputStream[] getInputStreamsFromFiles(File[] files) {
        InputStream[] inputStreams = new InputStream[files.length];

        try {
            for (int i = 0; i < files.length ; i++) {
                inputStreams[i] = new FileInputStream(files[i]);
            }
        } catch (FileNotFoundException ex) {
            for (int i = 0; i < inputStreams.length; i++) {
                try {
                    if (inputStreams[i] != null) {
                        inputStreams[i].close();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.FINE, null, e);
                }
            }
            LOGGER.log(Level.INFO, null, ex);
            return new InputStream[] {};
        }

        return inputStreams;
    }
    
    private class OC4JLoggerRunnable implements Runnable {
        
        private InputStream inputStream;
        
        public OC4JLoggerRunnable(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        
        /**
         * Implementation of the Runnable interface. Here all tailing is
         * performed
         */
        public void run() {
            try {
                // create a reader from the input stream
                InputStreamReader reader = new InputStreamReader(inputStream);
                
                // read from the input stream and put all the changes to the
                // I/O window
                char[] chars = new char[1024];
                while (true) {
                    // while there is something in the stream to be read - read that
                    while (reader.ready()) {
                        write(new String(chars, 0, reader.read(chars)));
                        selectIO();
                    }
                    
                    // when the stream is empty - sleep for a while
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            } catch (IOException e) {
                Logger.getLogger("global").log(Level.WARNING, null, e);
            } finally {
                // close the opened stream
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.getLogger("global").log(Level.WARNING, null, e);
                }
            }
        }
    }
}