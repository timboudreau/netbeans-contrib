/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsAutoFillConfig extends Object implements VcsAdditionalCommand {
    
    private static final String CVS_DIR = "CVS";
    private static final String CVS_ROOT = "Root";
    private static final String CVS_LOCAL = "local";

    /** Creates new CvsAutoFillConfig */
    public CvsAutoFillConfig() {
    }
    
    private static File lookForCVSRoot(String dirName) {
        File cvsRoot = new File(dirName, CVS_DIR + File.separator + CVS_ROOT);
        if (!cvsRoot.exists()) {
            String cvsRootName = CVS_DIR + File.separator + CVS_ROOT;
            File[] subfiles = new File(dirName).listFiles();
            if (subfiles != null) {
                for (int i = 0; i < subfiles.length; i++) {
                    if (subfiles[i].isDirectory()) {
                        File test = new File(subfiles[i], cvsRootName);
                        if (test.exists()) {
                            cvsRoot = test;
                            break;
                        }
                    }
                }
            }
        }
        return cvsRoot;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        
        String dirName = (String) vars.get("ROOTDIR");
        String relMount = (String) vars.get("MODULE");
        if (relMount.length() > 0) {
            dirName += File.separator + relMount;
        }
        File dirFile = lookForCVSRoot(dirName);
        if (!dirFile.exists()) dirFile = lookForCVSRoot((String) vars.get("ROOTDIR"));
        String serverType = null;
        String repository = null;
        String userName = null;
        String serverName = null;
        String serverPort = null;
        if (dirFile.exists()) {
            BufferedReader buff = null;
            try {
                buff = new BufferedReader(new InputStreamReader(new FileInputStream(dirFile.getAbsolutePath())));
                String line = buff.readLine();
                if (line != null && line.startsWith(":")) {
                    line = line.substring(1);
                    //StringTokenizer token = new StringTokenizer(line, ":",false);
                    int begin = 0;
                    int end = line.indexOf(":");
                    if (end > 0) {
                        serverType = line.substring(begin, end);
                        if (serverType.equals(CVS_LOCAL))  {
                            repository =  line.substring(end + 1);
                            userName = "";
                            serverName = "";
                        } else {   //some kind of server..
                            begin = end + 1;
                            end = line.indexOf(":", begin);
                            if (begin < line.length()) {
                                String userServer =  line.substring(begin, (end > 0) ? end : line.length());
                                int atIndex = userServer.indexOf('@');
                                if (atIndex >= 0) {
                                    userName = userServer.substring(0, atIndex);
                                    serverName = userServer.substring(atIndex + 1);
                                } else {
                                    userName = "";
                                    serverName = userServer;
                                }
                            }
                            if (end > 0) repository = line.substring(end + 1);
                            StringBuffer port = new StringBuffer();
                            char c;
                            for (int i = 0; repository.length() > i && Character.isDigit(c = repository.charAt(i)); i++) {
                                port.append(c);
                            }
                            if (port.length() > 0) {
                                serverPort = port.toString();
                                repository = repository.substring(port.length());
                            }
                        }
                    }
                }
            } catch (IOException exc) { //doesn't matter - nothing will be filled in
            }
            finally {
                if (buff != null) {
                    try {
                        buff.close();
                    } catch (IOException eIO) {}
                }
            }
        }
        if (serverType != null) vars.put("SERVERTYPE", serverType);
        if (repository != null) vars.put("CVS_REPOSITORY", repository);
        if (userName != null) vars.put("CVS_USERNAME", userName);
        if (serverName != null) vars.put("CVS_SERVER", serverName);
        if (serverPort != null) vars.put("ENVIRONMENT_VAR_CVS_CLIENT_PORT", serverPort);
        else vars.remove("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        return true;
    }
}
