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
import org.netbeans.lib.cvsclient.CVSRoot;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsAutoFillConfig extends Object implements VcsAdditionalCommand {
    
    private static final String CVS_DIR = "CVS"; // NOI18N
    private static final String CVS_ROOT = "Root"; // NOI18N
    private static final String CVS_LOCAL = "local"; // NOI18N
    private static final String CVS_EXT = "ext";  // NOI18N

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
                CVSRoot cvsroot = null;
                try {
                    cvsroot = CVSRoot.parse(line);
                    serverType = cvsroot.getMethod();
                    if (serverType == null) {
                        if (cvsroot.isLocal()) {
                            serverType = CVS_LOCAL;
                        } else {
                            serverType = CVS_EXT;
                        }
                    }
                    repository = cvsroot.getRepository();
                    userName = cvsroot.getUserName();
                    serverName = cvsroot.getHostName();
                    int port = cvsroot.getPort();
                    if (port > 0) {
                        serverPort = Integer.toString(port);
                    }
                } catch (IllegalArgumentException iaex) { //doesn't matter - nothing will be filled in
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
        vars.remove("BUILT-IN"); // Not to alter that variable
        return true;
    }
}
