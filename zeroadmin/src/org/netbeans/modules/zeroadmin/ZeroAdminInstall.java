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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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

package org.netbeans.modules.zeroadmin;

import java.beans.PropertyChangeListener;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.xerces.impl.dv.util.Base64;
import org.netbeans.core.startup.layers.SessionManager;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.*;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;

import org.netbeans.core.NbTopManager;
import org.netbeans.modules.remotesfs.MemoryFileSystem;
import org.openide.NotifyDescriptor;

/**
 * This class is needed because
 *   1. we hack to core
 *   2. we start a periodic saving task.
 *   3. we retrieve and store configuration remotely
 * @author David Strupl
 * @author CLi
 */
public class ZeroAdminInstall extends ModuleInstall implements PropertyChangeListener {
    
    /**
     * RequestProcessor for RemoteSaver.
     */
    static RequestProcessor RP = new RequestProcessor("org.netbeans.modules.ZeroAdmin.Saver");
    
    /**
     * Private instance used for more fine grained logging.
     */
    private static final Logger log = Logger.getLogger(ZeroAdminInstall.class.getName());
    
    /**
     * Filesystem used for writing user settings. Originally this
     * is in ${user.dir}/system. 
     */
    public FileSystem writableLayer;
    
    /**
     * Task for periodic saving.
     * (public for access from tests)
     */
    public RequestProcessor.Task saver;
    
    /**
     * HTTP(s) configuration proxy.
     */
    public ConfigProxy cfgProxy;

    /**
     * Used to tell saver not to continue.
     */
    private boolean finished;
   
    /**
     * Save interval
     */
    private int interval;
    
    public void restored () {
        try {
            SessionManager.getDefault().addPropertyChangeListener(this);
            load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Connects us to the server. Initializes cfgProxy variable.
     * Starts periodic saving by installing RemoteSaver.
     */
    public void load() throws IOException {
        try {
            // Set URL timeouts (only if not set already)
            String defConnTimeout = System.getProperty("sun.net.client.defaultConnectTimeout","120000");
            System.setProperty("sun.net.client.defaultConnectTimeout",defConnTimeout);
            log.info("[zeroadmin] sun.net.client.defaultConnectTimeout = " + defConnTimeout);

            String defReadTimeout = System.getProperty("sun.net.client.defaultReadTimeout","120000");
            System.setProperty("sun.net.client.defaultReadTimeout",defReadTimeout);
            log.info("[zeroadmin] sun.net.client.defaultReadTimeout = " + defReadTimeout);
            
            StatusDisplayer.getDefault().setStatusText(NbBundle.getBundle(ZeroAdminInstall.class).getString("MSG_StartLoading"));
            Hashtable t = new Hashtable();

            // Initialize remote connection to ZeroAdminServlet
            String urlStr = System.getProperty("netbeans.config.url", "appres:8080//zeroadmin/desktop/nbconfig");
            log.info("[zeroadmin] netbeans.config.url = " + urlStr);

            interval = Integer.parseInt( 
                System.getProperty("netbeans.config.interval", "60000") );
            log.info("[zeroadmin] netbeans.config.interval = " + interval);

            cfgProxy = new ConfigProxy(new URL(urlStr));
            
            // Activate zeroadmin only if interval is > 0
            if (interval > 0)
            {
                cfgProxy.init();
                initRemoteSaving();
    
                if (! installUserData()) {
                    // in case there are no user data - take the defaults
                    installOperatorData();
                }
                
                Runnable r = new RemoteSaver();
                saver = RP.post(r, interval);
            }
    
            StatusDisplayer.getDefault().setStatusText(NbBundle.getBundle(ZeroAdminInstall.class).getString("MSG_FinishLoading"));
        } catch (Exception x) {
            log.log(Level.SEVERE, "", x);
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                NbBundle.getBundle(ZeroAdminInstall.class).getString("MSG_FailedToInitialize"),
                NotifyDescriptor.ERROR_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
            DialogDescriptor dd = new DialogDescriptor(
                NbBundle.getBundle(ZeroAdminInstall.class).getString("MSG_Closing"),
                NbBundle.getBundle(ZeroAdminInstall.class).getString("TITLE_Closing"),
                false, DialogDescriptor.OK_CANCEL_OPTION, null,
                new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        RP.post(new Runnable() {
                            public void run() {
                                System.exit(0);
                            }
                        }, 1000);
                    }
                }
            );
            
            DialogDisplayer.getDefault().notify(dd);
        }
    }
    
    /**
     * Sets the finished flag to true
     * not to start the saver thread again.
     * Schedules the last run of the Saver and
     * waits until it is finished.
     */
    public void exiting() throws IOException {
        // shut down stuff
        finished = true;
        if (saver != null) {
            saver.schedule(0);
            // wait to be sure that the last state is saved
            saver.waitFinished();
            saver.run();
        }
    }
    
    // ============================================================

    /**
     * Finds the writable layer on the system file system and stores
     * it to the writableLayer variable.
     */
    private void initRemoteSaving() throws Exception {
        log.fine("initRemoteSaving");
        if (!cfgProxy.isConnected()) {
            // unable to operate
            log.fine("Not connected - unable to initRemoteSaving.");
            return;
        }
        writableLayer = MemoryFileSystem.getInstance();
        log.fine("Writable layer successfully initialized.");
    }
    
    /** Retrieves the data from the server and installs them to the
     * system file system.
     * @throws Exception
     * @return true if successfully installed, false otherwise
     */
    private boolean installUserData() throws Exception {
        log.entering(getClass().getName(), "installUserData");
        if (!cfgProxy.isConnected()) {
            log.exiting(getClass().getName(), "installUserData1", false);
            return false;
        }

        char[] data = cfgProxy.getUserData();
        if (data == null) {
            log.exiting(getClass().getName(), "installUserData2", false);
            return false;
        }

        final XMLBufferFileSystem bufFs = new XMLBufferFileSystem(new ParseRegen(data));
        writableLayer.runAtomicAction(new FileSystem.AtomicAction() {
            // atomic action --> should be faster???
            public void run() throws IOException {
                copy(bufFs.getRoot(), writableLayer.getRoot(), true);
            }
        });
        log.exiting(getClass().getName(), "installUserData", true);
        return true;
    }
    
    /**
     * Retrieves the data from the server and installs them to the
     * system file system.
     */
    public void installOperatorData() throws Exception {

        if (!cfgProxy.isConnected()) {
            return;
        }
        char[] data = cfgProxy.getOperatorData();
        if (data == null) {
            return;
        }

        // copy the opeartors data to the writable layer
        final XMLBufferFileSystem bufFs = new XMLBufferFileSystem(new ParseRegen(data));
        writableLayer.runAtomicAction(new FileSystem.AtomicAction() {
            // atomic action --> should be faster???
            public void run() throws IOException {
                copy(bufFs.getRoot(), writableLayer.getRoot(), true);
            }
        });
    }
    
    /**
     * Retrieves the data from the server and installs them to the
     * system file system.
     */
    public void refreshOperatorData() throws Exception {

        if (!cfgProxy.isConnected()) {
            return;
        }
        char[] data = cfgProxy.getOperatorData();
        if (data == null) {
            return;
        }

        // copy the opeartors data to the writable layer
        final XMLBufferFileSystem bufFs = new XMLBufferFileSystem(new ParseRegen(data));
        writableLayer.runAtomicAction(new FileSystem.AtomicAction() {
            // atomic action --> should be faster???
            public void run() throws IOException {
                // this is temporary before the modules system is finished
                copy(bufFs.getRoot(), writableLayer.getRoot(), false);
            }
        });
    }
    
    /** Recursively copy contents of the srcFolder to the destFolder.
     * I was surprised but the regular FileObject's copy does not do this.
     * @param srcFolder from where to copy
     * @param destFolder - target folder for the copy
     * @param overwrite true - the files on the destination folder will be overwritten
     * @throws IOException
     */
    public static void copy(FileObject srcFolder, FileObject destFolder, boolean overwrite) throws IOException {
        Enumeration srcFolderAttrNames = srcFolder.getAttributes();
        while (srcFolderAttrNames.hasMoreElements()) {
            String s = (String)srcFolderAttrNames.nextElement();
            destFolder.setAttribute(s, srcFolder.getAttribute(s));
        }
        
        FileObject toCopy[] = srcFolder.getChildren();
        for (int i = 0; i < toCopy.length; i++) {
            if (toCopy[i].isData()) {
                try {
                    FileObject potentialConflict = destFolder.getFileObject(toCopy[i].getName(), toCopy[i].getExt());
                    if (potentialConflict != null) {
                        if (overwrite) {
                            potentialConflict.delete();
                        } else {
                            continue;
                        }
                    }
                    toCopy[i].copy(destFolder, toCopy[i].getName(), toCopy[i].getExt());
                } catch (IOException x) {
                    log.fine("Cannot copy " +
                        toCopy + " to " + destFolder + " on " + destFolder.getFileSystem());
                }
            } else {
                if (toCopy[i].isFolder()) {
                    FileObject d = destFolder.getFileObject(toCopy[i].getName(), 
                        toCopy[i].getExt());
                    if (d == null) {
                        try {
                            d = destFolder.createFolder(toCopy[i].getNameExt());
                            Enumeration attrNames = toCopy[i].getAttributes();
                            while (attrNames.hasMoreElements()) {
                                String s = (String)attrNames.nextElement();
                                d.setAttribute(s, toCopy[i].getAttribute(s));
                            }
                        } catch (IOException x) {
                            log.fine("Cannot create folder " +
                                toCopy[i].getNameExt() + " in " + destFolder + " on " + destFolder.getFileSystem());
                            continue;
                        }
                    }
                    if (d.isFolder()) {
                        // recursive call
                        copy(toCopy[i], d, overwrite);
                    } else {
                        log.fine("Cannot create folder in place of file " + d);
                    }
                }
            }
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        if (SessionManager.PROP_CLOSE.equals(propertyChangeEvent.getPropertyName())) {
            try {
                exiting();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    
    /**
     * Peridically saves the data from the writableLayer
     * to the server using storage.
     */
    private class RemoteSaver implements Runnable {
        
        public void run() {
            try {
                
                if (! SwingUtilities.isEventDispatchThread()) {
                    if (! finished) {
                        SwingUtilities.invokeLater(this);
                    }
                    return;
                }
                // force the core to save pending stuff:
                NbTopManager.WindowSystem windowSystem = (NbTopManager.WindowSystem)Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
                windowSystem.save();
                
                XMLBufferFileSystem bufFs = new XMLBufferFileSystem();
                copy(writableLayer.getRoot(), bufFs.getRoot(), true);
                
                bufFs.waitFinished();
                cfgProxy.saveUserData(bufFs.getBuffer());

            } catch (Exception re) {
                log.log(Level.SEVERE, "", re);
                NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getBundle(ZeroAdminInstall.class).getString("MSG_SavingToServerFailed"),
                    NotifyDescriptor.ERROR_MESSAGE
                );
            }
            // schedule myself again after 20 seconds
            if (! finished) {
                saver.schedule(20000);
            }
        }
    }

    /**
     * Handles remote connection to ZeroAdminServlet.
     */
    public class ConfigProxy {
        
        private final URL _cfgURL;
        private final String _userName;
        private boolean _connected = false;
        
        public ConfigProxy(URL cfgURL) throws IOException {
            _cfgURL = cfgURL;
            _userName = System.getProperty("netbeans.appuser", "operator");
            
            String apphost = System.getProperty("netbeans.apphost", "localhost");
            log.info("[zeroadmin] netbeans.apphost = " + apphost);
            log.info("[zeroadmin] netbeans.appuser = " + _userName);
        }
        
        public void init() throws IOException {
            _connected = true;
        }

        public boolean isConnected() {
            return _connected;
        }

        public void saveUserData(char[] data) throws IOException {
            postData(_userName, encodeData(data));
        }
        
        public void saveOperatorData(char[] data) throws IOException {
            postData("operator", encodeData(data));
        }
        
        public char[] getUserData() throws IOException {
            String data = getData(_userName);

            if(data != null && data.length() > 0) {
                return decodeData(data);
            } else {
                return null;
            }
        }
        
        public char[] getOperatorData() throws IOException {
            String data = getData("operator");

            if(data != null && data.length() > 0) {
                return decodeData(data);
            } else {
                return null;
            }
        }

        /**
         * Encode the given character data to Base64 format.
         */
        private String encodeData(char[] data) throws IOException {
            String cfg = null;

            try {
                byte[] buf = String.valueOf(data).getBytes("UTF-8");
                cfg = Base64.encode(buf);
            } catch( Exception e ) {
                throw new IOException(e.toString());
            }
            
            return cfg;
        }
        
        /**
         * Decodes the given Base64 string character data.
         */
        private char[] decodeData(String data) throws IOException {
            byte[] buf = Base64.decode(data);
            return new String(buf,"UTF-8").toCharArray() ;
        }
        
        /**
         * Post data to HTTP(s) server.
         */
        private void postData(String user, String data) throws IOException
        {
            try
            {
                StringBuffer body = new StringBuffer(URLEncoder.encode("user", "UTF-8"));
                body.append("=");
                body.append(URLEncoder.encode(user, "UTF-8"));
                body.append("&");
                body.append(URLEncoder.encode("cfg", "UTF-8"));
                body.append("=");
                body.append(URLEncoder.encode(data, "UTF-8"));
                body.append("&");
                body.append(URLEncoder.encode("mode", "UTF-8"));
                body.append("=");
                body.append(URLEncoder.encode("save", "UTF-8"));

                URLConnection cfgConn = _cfgURL.openConnection();
                cfgConn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(cfgConn.getOutputStream());
                wr.write(body.toString());
                wr.flush();
                
                // Handle response
                BufferedReader rd = new BufferedReader(
                                                new InputStreamReader(
                                                        cfgConn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null);
                wr.close();
                rd.close();

            } catch(Exception e) {
                throw new IOException( e.toString() );
            }
        }

        /**
         * Get data from HTTP(s) server.
         */
        private String getData(String user) throws IOException
        {
            StringBuffer data = new StringBuffer( "" );
            try
            {
                StringBuffer body = new StringBuffer(URLEncoder.encode("user", "UTF-8"));
                body.append("=");
                body.append(URLEncoder.encode(user, "UTF-8"));
                body.append("&");
                body.append(URLEncoder.encode("mode", "UTF-8"));
                body.append("=");
                body.append(URLEncoder.encode("load", "UTF-8"));

                URLConnection cfgConn = _cfgURL.openConnection();
                cfgConn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(cfgConn.getOutputStream());
                wr.write(body.toString());
                wr.flush();
                
                // Handle response
                BufferedReader rd = new BufferedReader(
                                                new InputStreamReader(
                                                        cfgConn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    data.append(line);
                }
                wr.close();
                rd.close();

            } catch(Exception e) {
                IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
            
            return data.toString();
        }
    }
}
