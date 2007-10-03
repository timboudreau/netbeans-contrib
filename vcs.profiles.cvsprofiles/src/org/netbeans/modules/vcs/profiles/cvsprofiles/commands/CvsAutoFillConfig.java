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
        
        File dirFile = lookForCVSRoot(dirName);
        if (!dirFile.exists()) dirFile = lookForCVSRoot((String) vars.get("ROOTDIR"));
        String serverType = null;
        String repository = null;
        String userName = null;
        String serverName = null;
        String serverPort = null;
        boolean isDoCheckoutVar = vars.keySet().contains("DO_CHECKOUT");
        vars.clear(); // Not to alter other variables than that we want to set.
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
            if (isDoCheckoutVar) vars.put("DO_CHECKOUT",""); // NOI18N
        } else {
            if (isDoCheckoutVar) vars.put("DO_CHECKOUT","true");// NOI18N
        }
        if (serverType != null) vars.put("SERVERTYPE", serverType);// NOI18N
        if (repository != null) vars.put("CVS_REPOSITORY", repository);// NOI18N
        if (userName != null) vars.put("CVS_USERNAME", userName);// NOI18N
        if (serverName != null) vars.put("CVS_SERVER", serverName);// NOI18N
        if (serverPort != null) vars.put("ENVIRONMENT_VAR_CVS_CLIENT_PORT", serverPort);// NOI18N
        else vars.remove("ENVIRONMENT_VAR_CVS_CLIENT_PORT");// NOI18N
        vars.remove("BUILT-IN"); // Not to alter that variable   // NOI18N         
        return true;
    }
}
