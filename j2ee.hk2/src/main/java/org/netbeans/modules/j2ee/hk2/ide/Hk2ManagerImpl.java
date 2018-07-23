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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.hk2.ide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.progress.ProgressEventSupport;
import org.netbeans.modules.j2ee.hk2.progress.Status;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/** Implementation of management task that provides info about progress
 *
 */
public class Hk2ManagerImpl implements ProgressObject, Runnable {
    
    /** RequestProcessor processor that serializes management tasks. */
    private static RequestProcessor rp;
    
    /** Returns shared RequestProcessor. */
    private static synchronized RequestProcessor rp() {
        if (rp == null) {
            rp = new RequestProcessor("GlassFish V3 management", 1); // NOI18N
        }
        return rp;
    }
    
    /** Support for progress notifications. */
    private ProgressEventSupport pes;
    
    /** Command that is executed on running server. */
    private String command;
    
    /** Output of executed command (parsed for list commands). */
    private String outputMessage;
    /** code of executed command (SUCCESS or FAILURE). */
    private String outputCode;
    
    private List<String> tmidNames;
    private List<String> outputContainers;
    
    /** Command type used for events. */
    private CommandType cmdType;
    
    /** InputStream of application data. */
    private InputStream istream;
    
    private Hk2DeploymentManager tm;
    
    /** Has been the last access to  manager web app authorized? */
    private boolean authorized;
    
    /** TargetModuleID of module that is managed. */
    private Hk2TargetModuleID tmId;
    
    public Hk2ManagerImpl(Hk2DeploymentManager tm) {
        this.tm = tm;
        pes = new ProgressEventSupport(this);
    }
    
    /*calculate the module name form the dir (parent/parent for build/web parent dir for the proje
     * ject name
     */
      
    public void initialDeploy(Target t,  File dir)  {
        initialDeploy(t,dir, dir.getParentFile().getParentFile().getName());
    }
    
    public void initialDeploy(Target t,  File dir, String moduleName)  {
        try {
            //file is someting like /Users/ludo/WebApplication91/build/web
            String docBaseURI = URLEncoder.encode(dir.getAbsoluteFile().toURI().toASCIIString(),"UTF-8");
            String docBase = moduleName;
            String ctxPath = docBase;///ctx.getAttributeValue ("path");
            this.tmId = new Hk2TargetModuleID(t, ctxPath, docBase); //NOI18N
            
//            command = "deploy?path=" + dir.getAbsoluteFile()+"?name="+docBaseURI; // NOI18N
            command = "deploy?path=" + dir.getAbsoluteFile()+"?name="+docBase+"?force=true"; // NOI18N
            
            cmdType = CommandType.DISTRIBUTE;
//            System.out.println("deploy command="+command);
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeploymentInProgress");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.RUNNING));
            rp().post(this, 0, Thread.NORM_PRIORITY);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeployBrokenContextXml");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.FAILED));
        } catch (RuntimeException e) {
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeployBrokenContextXml");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.FAILED));
        }
    }
    
    public void reDeploy(TargetModuleID targetModuleID)  {
        try {
            
            this.tmId = (Hk2TargetModuleID) targetModuleID;
            
            command = "deploy?name=" +targetModuleID.getModuleID(); // NOI18N
            
            String path = getModulePath(targetModuleID.getModuleID());
            if(path != null && path.length() > 0) {
                if(path.startsWith("file:")) {
                    path = path.substring(5);
                }
                command += "?path=" + path + "?force=true";
            } else {
                Logger.getLogger("glassfish-hk2").log(Level.WARNING, 
                        "Unable to locate module path for " + targetModuleID.getModuleID());
            }
            
            
            cmdType = CommandType.DISTRIBUTE;
//            System.out.println("redeploy command="+command);
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeploymentInProgress");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.RUNNING));
            rp().post(this, 0, Thread.NORM_PRIORITY);
            
        } catch (RuntimeException e) {
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeployBrokenContextXml");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.FAILED));
        }
    }
    
    public void stopServer(Target t) {
        try {
            
            command = "stop-domain"; // NOI18N
            
            cmdType = CommandType.STOP;
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeploymentInProgress");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.RUNNING));
            rp().post(this, 0, Thread.NORM_PRIORITY);
            // } catch (java.io.IOException ioex) {
            //     pes.fireHandleProgressEvent (null, new Status (ActionType.EXECUTE, cmdType, ioex.getLocalizedMessage (), StateType.FAILED));
        } catch (RuntimeException e) {
            String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_DeployBrokenContextXml");
            pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.FAILED));
        }
    }
    public  TargetModuleID[] getTargetModuleID(Target t){
        
        command = "list-applications"; // NOI18N
//        System.out.println("in getTargetModuleID imple.....");
        cmdType = CommandType.DISTRIBUTE;
        run();
//        System.out.println("tmidNames" + tmidNames);
        if (tmidNames==null){
            return null;
        }
        TargetModuleID ret[] = new TargetModuleID[tmidNames.size()];
        for (int i=0;i< tmidNames.size();i++){
            ret[i]=new Hk2TargetModuleID(t,tmidNames.get(i),tmidNames.get(i));
        }
        
        
        return ret;
    }
    
    public boolean isV3Running() {
        command = "version";
        cmdType = CommandType.DISTRIBUTE;
        run();
        return "SUCCESS".equals(outputCode);
    }
    
    public void undeploy(Hk2TargetModuleID tmId) {
        
        this.tmId = tmId;
        command = "undeploy?name="+tmId.getModuleID(); // NOI18N
        cmdType = CommandType.UNDEPLOY;
        String msg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_UndeploymentInProgress");
        pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, msg, StateType.RUNNING));
        rp().post(this, 0, Thread.NORM_PRIORITY);
    }
    
    
    
    
    
    /**
     * Translates a context path string into <code>application/x-www-form-urlencoded</code> format.
     */
    private static String encodePath(String str) {
        try {
            StringTokenizer st = new StringTokenizer(str, "/"); // NOI18N
            if (!st.hasMoreTokens()) {
                return str;
            }
            StringBuilder result = new StringBuilder();
            while (st.hasMoreTokens()) {
                result.append("/").append(URLEncoder.encode(st.nextToken(), "UTF-8")); // NOI18N
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); // this should never happen
        }
    }
    
    
    
    
    
    /** JSR88 method. */
    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
        return null; // PENDING
    }
    
    /** JSR88 method. */
    public DeploymentStatus getDeploymentStatus() {
        return pes.getDeploymentStatus();
    }
    
    /** JSR88 method. */
    public TargetModuleID[] getResultTargetModuleIDs() {
        return new TargetModuleID [] { tmId };
    }
    
    /** JSR88 method. */
    public boolean isCancelSupported() {
        return false;
    }
    
    /** JSR88 method. */
    public void cancel()
    throws OperationUnsupportedException {
        throw new OperationUnsupportedException("cancel not supported in hk2 deployment"); // NOI18N
    }
    
    /** JSR88 method. */
    public boolean isStopSupported() {
        return false;
    }
    
    /** JSR88 method. */
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("stop not supported in hk2 deployment"); // NOI18N
    }
    
    /** JSR88 method. */
    public void addProgressListener(ProgressListener l) {
        pes.addProgressListener(l);
    }
    
    /** JSR88 method. */
    public void removeProgressListener(ProgressListener l) {
        pes.removeProgressListener(l);
    }
    
    private void analyseServerOutput(InputStream fis){
        Manifest m = new Manifest();
        try {
            m.read(fis);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        System.out.println("m"+m);
        tmidNames = new ArrayList();
        outputContainers = new ArrayList();
        
        outputCode = m.getMainAttributes().getValue("exit-code");
        outputMessage = m.getMainAttributes().getValue("message");
        if(outputMessage==null){
            outputMessage="";
        }
        Logger.getLogger("glassfish-hk2").log(Level.INFO, "command result: " + outputCode + ", " + outputMessage);
        
//        System.out.println("Exit code is " + outputCode);
        if (!outputCode.equalsIgnoreCase("Success")) {
//            System.out.println("message-> " + outputMessage);
            pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, outputMessage, StateType.FAILED));
            return;
        }else {
            pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, outputMessage, StateType.COMPLETED));
            
        }
        
        String containers = m.getMainAttributes().getValue("children");
        if (containers==null) {
            // no container currently started.
            return;
        }
        StringTokenizer token = new StringTokenizer(containers, ",");
        while (token.hasMoreTokens()) {
            String container = token.nextToken();
//            System.out.println("Container : " + container);
            outputContainers.add(container);
            // get container attributes
            Attributes contAttr = m.getAttributes(container);
            String apps = contAttr.getValue("children");
            if (apps==null) {
                // no app currently deployed in this container
                continue;
            }
            StringTokenizer appsToken = new StringTokenizer(apps, ",");
            while (appsToken.hasMoreTokens()) {
                String app = appsToken.nextToken();
                //  tmidNames.add(app);
                Attributes appAttr = m.getAttributes(app);
//                System.out.println("Module deployed " + appAttr.getValue("message"));
                tmidNames.add(appAttr.getValue("message"));
            }
        }
    }
    
    /** Executes one management task. */
    public synchronized void run() {
        pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, command , StateType.RUNNING));
        
        outputMessage = "";
        authorized = true;
        
        int retries = command.startsWith("version") ? 1 : 4;
        
        URLConnection conn = null;
        InputStreamReader reader = null;
        
        URL urlToConnectTo = null;
        
        boolean failed = false;
        String msg = "";
        while (retries >= 0) {
            retries = retries - 1;
            try {
                
                // Create a connection for this command
                String uri = tm.getPlainUri();
                String withoutSpaces = (uri + command).replaceAll(" ", "%20");  //NOI18N
                Logger.getLogger("glassfish-hk2").log(Level.INFO, "command: " + withoutSpaces);
                urlToConnectTo = new URL(withoutSpaces);
//                System.out.println("withoutSpaces  "+withoutSpaces);
                
                
                conn = urlToConnectTo.openConnection();
                HttpURLConnection hconn = (HttpURLConnection) conn;
                
                // Set up standard connection characteristics
                hconn.setAllowUserInteraction(false);
                hconn.setDoInput(true);
                hconn.setUseCaches(false);
                if (istream != null) {
                    hconn.setDoOutput(true);
                    hconn.setRequestMethod("PUT");   // NOI18N
                    hconn.setRequestProperty("Content-Type", "application/octet-stream");   // NOI18N
                } else {
                    hconn.setDoOutput(false);
                    hconn.setRequestMethod("GET"); // NOI18N
                }
                hconn.setRequestProperty("User-Agent", "hk2-agent"); // NOI18N
                // Set up an authorization header with our credentials
////                Hk2Properties tp = tm.getHk2Properties();
////                String input = tp.getUsername () + ":" + tp.getPassword ();
////                String auth = new String(Base64.encode(input.getBytes()));
////                hconn.setRequestProperty("Authorization", // NOI18N
////                                         "Basic " + auth); // NOI18N
                
                // Establish the connection with the server
                hconn.connect();
                int respCode = hconn.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_UNAUTHORIZED
                        || respCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    // connection to manager has not been allowed
                    authorized = false;
                    String errMsg = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_AuthorizationFailed");
                    pes.fireHandleProgressEvent(null, new Status(ActionType.EXECUTE, cmdType, errMsg, StateType.FAILED));
                    return;
                }
                if (Boolean.getBoolean("org.netbeans.modules.hk2.LogManagerCommands")) { // NOI18N
                    int code = hconn.getResponseCode();
                    String message = "  receiving response, code: " + code;
//                    System.out.println(message);
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new Exception(message));
                }
                // Send the request data (if any)
                if (istream != null) {
                    BufferedOutputStream ostream =
                            new BufferedOutputStream(hconn.getOutputStream(), 1024);
                    byte buffer[] = new byte[1024];
                    while (true) {
                        int n = istream.read(buffer);
                        if (n < 0) {
                            break;
                        }
                        ostream.write(buffer, 0, n);
                    }
                    ostream.flush();
                    ostream.close();
                    istream.close();
                }
                
                // Process the response message
                analyseServerOutput(hconn.getInputStream());
                return;
//                reader = new InputStreamReader(hconn.getInputStream(),"UTF-8"); //NOI18N
//                retries = -1;
//                StringBuffer buff = new StringBuffer();
//                String error = null;
//                msg = "";
//                boolean first = !command.startsWith("jmxproxy");   // NOI18N
//                while (true) {
//                    int ch = reader.read();
//                    if (ch < 0) {
//                        outputMessage += buff.toString()+"\n";    // NOI18N
//                        break;
//                    } else if ((ch == '\r') || (ch == '\n')) {
//                        String line = buff.toString();
//                        buff.setLength(0);
//                        if (first) {
//                            // hard fix to accept the japanese localization of manager app
//                            String japaneseOK="\u6210\u529f"; //NOI18N
//                            msg = line;
//                            // see issue #62529
//                            if (line.indexOf("java.lang.ThreadDeath") != -1) { // NOI18N
//                                String warning = NbBundle.getMessage(Hk2ManagerImpl.class, "MSG_ThreadDeathWarning");
//                                pes.fireHandleProgressEvent(
//                                        tmId,
//                                        new Status(ActionType.EXECUTE, cmdType, warning, StateType.RUNNING)
//                                        );
//                            } else if (!(line.startsWith("OK -") || line.startsWith(japaneseOK))) { // NOI18N
//                                ///////ludo   TOD redo error error = line;
//                            }
//                            first = false;
//                        }
//                        outputMessage += line+"\n";    // NOI18N
//                    } else {
//                        buff.append((char) ch);
//                    }
//                }
//
//                if (error != null) {
//
//                    pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, error, StateType.FAILED));
//                    failed = true;
//                }
                
            } catch (Exception e) {
                if (retries < 0) {
                    pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, e.getLocalizedMessage(), StateType.FAILED));
                    failed = true;
                }
                // throw t;
            } finally {
//                System.out.println("output is...:"+outputMessage);
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (java.io.IOException ioe) { // ignore this
                    }
                    reader = null;
                }
                if (istream != null) {
                    try {
                        istream.close();
                    } catch (java.io.IOException ioe) { // ignore this
                    }
                    istream = null;
                }
            }
            if (retries >=0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {}
            }
        } // while
        if (!failed) {
            pes.fireHandleProgressEvent(tmId, new Status(ActionType.EXECUTE, cmdType, msg, StateType.COMPLETED));
        }
    }
    
    
    private static final String DEFAULT_DOMAIN_DIR = "domains/domain1";
    private static final String DOMAIN_XML_PATH = "config/domain.xml";
    
    private String getModulePath(String moduleName) {
        String path = null;
        String installRoot = tm.getInstanceProperties().getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME);
        if(installRoot != null && installRoot.length() > 0) {
            File installDir = new File(installRoot);
            File domainDir = new File(installDir, DEFAULT_DOMAIN_DIR);
            Map<String, ModuleDesc> moduleList = loadDeployedModuleList(domainDir);
            ModuleDesc desc = moduleList.get(moduleName);
            path = (desc != null) ? desc.path : null;
        }
        return path;
    }
    
    private Map<String, ModuleDesc> loadDeployedModuleList(File domainDir) {
        Map<String, ModuleDesc> result = Collections.emptyMap();
        File domainXml = new File(domainDir, DOMAIN_XML_PATH);

        if (domainXml.exists()) {
            InputStream is = null;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                // !PW If namespace-aware is enabled, make sure localpart and
                // qname are treated correctly in the handler code.
                //                
                factory.setNamespaceAware(false);
                SAXParser saxParser = factory.newSAXParser();
                DomainXmlParser handler = new DomainXmlParser();
                is = new BufferedInputStream(new FileInputStream(domainXml));
                saxParser.parse(new InputSource(is), handler);
                result = handler.getModuleList();
            } catch (ParserConfigurationException ex) {
                // Badly formed domain.xml, fail.
            } catch (SAXException ex) {
                // Badly formed domain.xml, fail.
            } catch (IOException ex) {
                // Badly formed domain.xml, fail.
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        // ignore
                    }
                }
            }
        }

        return result;
    }
    
    private static final class ModuleDesc {
        public final String name;
        public final String path;
        
        public ModuleDesc(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
    
    /**
     * This is a weak parser that attempts to locate the http, https, and admin
     * port definitions in domain.xml.
     * 
     * Obvious improvements include accurate tracking of xml nodes to be certain
     * we read /domain/configs/config for the correct server (e.g. clusters) and
     * the correct http-listener entries therein.
     */
    private static final class DomainXmlParser extends DefaultHandler {

        // Parser state
        private Tag wantedTag;
        private Tag currentTag;
        
        // Results
        private Map<String, ModuleDesc> moduleMap;

        DomainXmlParser() {
        }

        @Override
        public void startElement(String uri, String localname, String qname, 
                org.xml.sax.Attributes attributes) throws SAXException {
            if(wantedTag.element().equals(qname)) {
                currentTag = wantedTag;
                wantedTag = wantedTag.next();
            } else {
                currentTag = null;
            }
            if(currentTag == Tag.APPLICATION) {
                // <application enabled="true" 
                //     context-root="/Foo" 
                //     location="file:/tmp/Foo/build/web/" 
                //     name="Foo" 
                //     directory-deployed="true" 
                //     object-type="user">                
                String name = attributes.getValue("name");
                if(name != null && name.length() > 0) {
                    String path = attributes.getValue("location");
                    moduleMap.put(name, new ModuleDesc(name, path));
                }
            }
        }
        
        @Override
        public void endElement(String uri, String localname, String qname) throws SAXException {
        }

        @Override
        public void startDocument() throws SAXException {
            wantedTag = Tag.DOMAIN;
            currentTag = null;
            moduleMap = new HashMap<String, ModuleDesc>();
        }

        @Override
        public void endDocument() throws SAXException {
        }
        
        public Map<String, ModuleDesc> getModuleList() {
            return moduleMap;
        }

        private static enum Tag {
            
            DOMAIN { 
                public String element() { return "domain"; } 
                public Tag next() { return APPLICATIONS; }
            },
            APPLICATIONS { 
                public String element() { return "applications"; } 
                public Tag next() { return APPLICATION; }
            },
            APPLICATION { 
                public String element() { return "application"; } 
                public Tag next() { return APPLICATION; }
            };
            
            public abstract String element();
            public abstract Tag next();
            
        }
        
    }
    
}
