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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.io.*;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 * Automatic fill of configuration for PVCS
 * @author  Martin Entlicher
 */
public class PvcsAutoFillConfig extends Object implements VcsAdditionalCommand {

    private static final String PVCS_CONFIG = "vcs.cfg";
    private static final String PVCS_INCLUDE = "INCLUDE";
    
    /** Creates new PvcsAutoFillConfig */
    public PvcsAutoFillConfig() {
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
        dirName += File.separator + PVCS_CONFIG;
        File dirFile = new File(dirName);
        String archive = null;
        if (dirFile.exists()) {
            BufferedReader buff = null;
            try {
                buff = new BufferedReader(new InputStreamReader(new FileInputStream(dirFile.getAbsolutePath())));
                String line;
                while ((line = buff.readLine()) != null) {
                    if (line.startsWith(PVCS_INCLUDE)) {
                        int begin = line.indexOf('"');
                        if (begin < 0) continue;
                        int endUnix = line.lastIndexOf('/');
                        if (endUnix < 0) endUnix = line.length();
                        int endWin = line.lastIndexOf('\\');
                        if (endWin < 0) endWin = line.length();
                        archive = line.substring(begin + 1, Math.min(endUnix, endWin)).trim();
                        break;
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
        if (archive != null) vars.put("PVCSROOT", archive);
        return true;
    }
}
