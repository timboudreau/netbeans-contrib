/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import org.openide.util.NbBundle;

import java.io.*;

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
                Process process = rt.exec(path.trim() + " -version"); // NOI18N
                process.waitFor();
                InputStream in = new BufferedInputStream(process.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                boolean foundCVS = false;
                boolean foundVersion = false;
                while (true) {
                    String line = r.readLine();
                    if (line == null) break;

                    // cvshome.org client
                    if (line.indexOf("CVS") != -1) foundCVS = true; // NOI18N
                    if (line.indexOf("1.11") != -1) foundVersion = true; // NOI18N
                    if (line.indexOf("1.12") != -1) foundVersion = true; // NOI18N
                }
                if (!foundCVS || !foundVersion) {
                    ret = getString("cvs_unsupported");
                }
            } catch (IOException e) {
                ret = path + " " + e.getLocalizedMessage();
            } catch (InterruptedException e) {
                ret = path + " " + e.getLocalizedMessage();
            }
        }
        return ret;
    }

    private static String getString(String key) {
        return NbBundle.getMessage(Validators.class, key);
    }
}
