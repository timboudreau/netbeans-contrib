/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * VALIDATOR_JAVA validators. Called by reflection from profile.
 *
 * @author Petr Kuzel
 */
public final class Validators {

    /** Test whether path points to supported cvs client. */
    public static final String wizardCvsExe(String path) {
        String ret = null;
        if (path == null || path.trim().length() == 0) {
            ret = getString("cvs_empty");
        } else {
            Runtime rt = Runtime.getRuntime();
            try {
                Process process = rt.exec(new String[] { path.trim(), "-version"}); // NOI18N
                RequestProcessor.getDefault().post(new ProcessWatcher(process), 100);
                process.waitFor();
                InputStream in = new BufferedInputStream(process.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                try {
                    boolean foundCVS = false;
                    boolean foundVersion = false;
                    String guessedVersion = ""; // NOI18N
                    while (true) {
                        String line = r.readLine();
                        if (line == null) break;

                        // cvshome.org client
                        int indexCVS = line.indexOf("CVS"); // NOI18N
                        if (indexCVS != -1 && !foundCVS) {
                            foundCVS = true;
                            if (line.length() > (indexCVS + 4)) {
                                guessedVersion = line.substring(indexCVS + 4);
                            }
                        }
                        if (line.indexOf("1.10") != -1) foundVersion = true; // NOI18N
                        if (line.indexOf("1.11") != -1) foundVersion = true; // NOI18N
                        if (line.indexOf("1.12") != -1) foundVersion = true; // NOI18N
                        // CVSNT client
                        int indexCVSNT = line.indexOf("CVSNT"); // NOI18N
                        if (indexCVSNT > 0 && line.indexOf("2.") != -1) foundVersion = true; // NOI18N
                    }
                    if (!foundCVS) {
                        ret = getString("not_cvs", path);
                    } else if (!foundVersion) {
                        ret = getString("cvs_unsupported", guessedVersion);
                    }
                } finally {
                    try {
                        r.close();
                    } catch (IOException ex) {
                        // elsready closed
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
            } catch (InterruptedException e) {
                ret = path + ": " + e.getLocalizedMessage();
            }
        }
        return ret;
    }
    
    private static String translate(String desc, String program) {
        // Translate a strange Windows error message:
        if (desc.startsWith("CreateProcess:") && desc.endsWith("error=2")) {
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
