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
import java.util.Map;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * VALIDATOR_JAVA validators. Called by reflection from profile.
 * Also added some public utility methods.
 *
 * @author Petr Kuzel, Martin Entlicher
 */
public final class Validators {

    private Validators() {
        throw new IllegalStateException("Do not instantiate.");
    }

    /** Test whether path points to supported cvs client. */
    public static final String wizardCvsExe(String path) {
        String ret = null;
        if (path == null || path.trim().length() == 0) {
            ret = getString("cvs_empty");
        } else {
            try {
                String[] CVS = getCVSVersion(path);
                if (CVS == null) {
                    ret = getString("not_cvs", path);
                } else {
                    if (CVS[1] == null) {
                        ret = getString("cvs_unsupported", CVS[0]);
                    } else {
                        if ("CVS".equals(CVS[0])) {
                            if (!(CVS[1].startsWith("1.10") ||
                                  CVS[1].startsWith("1.11") ||
                                  CVS[1].startsWith("1.12"))) {
                                ret = getString("cvs_unsupported", CVS[1]);
                            }
                        } else if ("CVSNT".equals(CVS[0])) {
                            if (!(CVS[1].startsWith("2."))) {
                                ret = getString("cvs_unsupported", CVS[1]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                ret = e.getLocalizedMessage();
                if (ret.startsWith(e.getClass().getName())) {
                    ret = ret.substring(e.getClass().getName().length());
                    if (ret.startsWith(":")) ret = ret.substring(1);
                    ret = ret.trim();
                } else if (ret.indexOf(path) == -1) {
                    ret = path + ": " + ret;
                }
                ret = translate(ret, path.trim());
            }
        }
        return ret;
    }
    
    /**
     * Get the version of the cvs executable.
     * @param path The path to the cvs executable
     * @return The array of size 2, 0. element is "CVS" or "CVSNT" or other name of the client,
     *         1. element is the version number or <code>null</code> when the
     *         version was not found.
     *         Or it returns <code>null</code> when it was not detected as a CVS client.
     * @throws IOException When there is a problem in the execution or output reading.
     */
    public static final String[] getCVSVersion(String path) throws IOException {
        String CVSClient = null;
        String version = null;
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(new String[] { path.trim(), "-version"}); // NOI18N
        RequestProcessor.getDefault().post(new ProcessWatcher(process), 100);
        InputStream in = new BufferedInputStream(process.getInputStream());
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        try {
            while (true) {
                String line = r.readLine();
                if (line == null) break;

                // CVS or CVSNT client
                int indexCVS = line.indexOf("(CVS"); // NOI18N
                if (indexCVS >= 0) {
                    int par = line.indexOf(')', indexCVS);
                    if (par > 0) {
                        CVSClient = line.substring(indexCVS + 1, par);
                        par++;
                        while (par < line.length() && Character.isWhitespace(line.charAt(par))) par++;
                        int indexVersion = par;
                        while (par < line.length() && !Character.isWhitespace(line.charAt(par))) par++;
                        version = line.substring(indexVersion, par);
                        break;
                    }
                } else {
                    // Some unknown CVS client
                    indexCVS = line.indexOf("CVS"); // NOI18N
                    if (indexCVS >= 0) {
                        int par = indexCVS + 3;
                        while (par < line.length() && !Character.isWhitespace(line.charAt(par))) par++;
                        CVSClient = line.substring(indexCVS, par);
                    }
                }
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                IOException ioex = new IOException("Interrupted.");
                ioex.initCause(e);
                throw ioex;
            }
        } finally {
            try {
                r.close();
            } catch (IOException ex) {
                // elsready closed
            }
        }
        if (CVSClient != null || version != null) {
            return new String[] { CVSClient, version };
        } else {
            return null;
        }
    }
    
    /**
     * Guess, whether we can rely on having the output streams merged in correct
     * order. <p>
     * The logic in the method is a result of tests of various configuration
     * options. It's possible then there exist a combination of settings
     * for which the streams are not merged correctly and which is not detected
     * by this method. It tries to be a conservative guess, but we can not be
     * sure about anything here.
     */
    public static boolean canHaveOutputStreamsMergedCorrectly(Map vars) {
        // On JDK 1.4 where we do not have the error stream merged in
        // for command-line clients, since Runtime.exec() does not have such ability.
        // The built-in client can merge the streams everytime.
        // And Command-line client in :local: connection can merge streams correctly
        // only if it's 1.11.19 version and higher (1.11.x series only) or if it's CVSNT
        // or if we do not run on Windows.
        boolean can = false;
        if (System.getProperty("java.version").startsWith("1.4")) { // NOI18N
            String builtIn = (String) vars.get("BUILT-IN"); // NOI18N
            can = "true".equals(builtIn); // Only when built-in on JDK 1.4.x
        } else { // JDK 1.5 and higher
            String servertype = (String) vars.get("SERVERTYPE");
            if ("local".equals(servertype) && Utilities.isWindows()) {
                try {
                    String[] version = Validators.getCVSVersion((String) vars.get("CVS_EXE"));
                    if (version != null && version[1] != null) {
                        //System.out.println("CVS Version: '"+version[0]+"', '"+version[1]+"'");
                        can = "CVSNT".equals(version[0]) ||
                              "CVS".equals(version[0]) && isCVSNewerThen19(version[1]);
                    }
                } catch (java.io.IOException ioex) {}
            } else {
                can = true; // *Hope* that this is true for all clients
            }
        }
        return can;
    }
    
    /** Test whether the version is newer then 1.11.19 (but 1.11.x series) */
    private static boolean isCVSNewerThen19(String version) {
        if (version.startsWith("1.11.")) {
            String no = version.substring("1.11.".length());
            try {
                int ver = Integer.parseInt(no);
                return ver >= 19;
            } catch (NumberFormatException nfex) {}
        }
        return false;
    }
    
    private static String translate(String desc, String program) {
        // Translate a strange Windows error message:
        if (desc.startsWith("CreateProcess:") && desc.indexOf(" error=") > 0) {
            return getString("CMDNotFound", program);
        }
        return desc;
    }

    private static String getString(String key) {
        return NbBundle.getMessage(Validators.class, key);
    }
    
    private static String getString(String key, Object param) {
        return NbBundle.getMessage(Validators.class, key, param);
    }
    
    /**
     * Watch the progress of the process. If it does not finish in time, kill
     * it forcibly so that we do not block the validation process.
     */
    private static final class ProcessWatcher extends Object implements Runnable {
        
        private Process process;
        
        public ProcessWatcher(Process process) {
            this.process = process;
        }
        
        public void run() {
            try {
                // Ask for the exit value to test whether the process has already finished
                process.exitValue();
            } catch (IllegalThreadStateException itsex) {
                // The process is still running. Kill it:
                process.destroy();
            }
        }
        
    }
}
