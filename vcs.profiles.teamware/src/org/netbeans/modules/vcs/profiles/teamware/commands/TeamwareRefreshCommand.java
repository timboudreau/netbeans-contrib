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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsListCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

public class TeamwareRefreshCommand extends VcsListCommand {

    public boolean list(Hashtable vars, String[] args,
        Hashtable filesByName,
        CommandOutputListener stdoutListener,
        CommandOutputListener stderrListener,
        CommandDataOutputListener stdoutDataListener,
        String dataRegex,
        CommandDataOutputListener stderrDataListener,
        String errorRegex) {
            
        File dir = TeamwareSupport.getDir(vars);
        File[] files = TeamwareRefreshSupport.listFilesInDir(dir);
        File sccsDir = new File(dir, "SCCS");
        for (int i = 0 ; i < files.length; i++) {
            String[] data = TeamwareRefreshSupport.listFile(files[i],
                sccsDir, stderrListener);
            if (data != null
                && !data[0].equals("Ignored")
                && !data[0].equals("Local")) {
                stdoutListener.outputLine(dir + File.separator + data[1] + " [" + data[0] + "]");
                filesByName.put(data[1], data);
            }
        }
        // Reset the toolbar buttons
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                Node[] nodes = TopComponent.getRegistry().getCurrentNodes();
                if (nodes != null) {
                    TopComponent.getRegistry().getActivated().setActivatedNodes(new Node[0]);
                    TopComponent.getRegistry().getActivated().setActivatedNodes(nodes);
                }
            }
        });
        return true;
    }
    
    
}
