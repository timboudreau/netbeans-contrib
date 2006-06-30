/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
