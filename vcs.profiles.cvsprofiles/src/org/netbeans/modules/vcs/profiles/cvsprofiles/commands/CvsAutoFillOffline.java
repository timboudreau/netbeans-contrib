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

import java.io.*;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 *
 * @author  Richard Gregor
 */
public class CvsAutoFillOffline extends Object implements VcsAdditionalCommand {
    
    /** Creates new CvsAutoFillOffline */
    public CvsAutoFillOffline() {
    }
    
  
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        
        
        String offline = (String)vars.get("FILESYSTEM_PROPERTY_offLine");//NOI18N
        if((offline != null)&&(offline.equals("true")))//NOI18N
            vars.put("DO_CHECKOUT","");//NOI18N
        return true;
    }
}
