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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.Table;

/**
 * Automatic fill of configuration for VSS
 * @author  Richard Gregor
 */
public class VssAutoFillConfig extends Object implements VcsAdditionalCommand {
    
  
    /** Creates new VssAutoFillConfig */
    public VssAutoFillConfig() {
    }

 
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                            
        String workdir = (String)vars.get("ROOTDIR");  //NOI18N
        if(workdir != null){
            if(workdir.length() > 0){
                File dir = new File(workdir);
                File[] children = dir.listFiles();
                if(children != null)
                    if(children.length > 0)
                        vars.put("DO_VSS_CHECKOUT",""); //NOI18N
                    else
                        vars.put("DO_VSS_CHECKOUT","true"); //NOI18N
            }
        }
           
        return true;
    }
    
   
}
