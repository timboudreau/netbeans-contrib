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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * Automatic fill of configuration for PVCS
 * @author  Martin Entlicher
 */
public class PvcsAutoFillConfig extends Object implements VcsAdditionalCommand,
                                                          RegexOutputListener {
    
    private static final String ERROR = " [Error]"; // NOI18N
    
    private VcsFileSystem fileSystem;
    private boolean failed;
    private String work;
    
    /** Creates new PvcsAutoFillConfig */
    public PvcsAutoFillConfig() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        
        if (args.length < 1) {
            stderrNRListener.outputLine("A get work location command name expected as an argument.");
            return false;
        }
        CommandSupport cmdSupp = fileSystem.getCommandSupport(args[0]);
        if (cmdSupp == null) {
            stderrNRListener.outputLine("Unknown command '"+args[0]+"'.");
            return false;
        }
        String projectDB = (String) vars.get("PROJECT_DB");
        if (projectDB == null || projectDB.length() == 0) return true;
        work = null;
        failed = false;
        Command cmd = cmdSupp.createCommand();
        ((RegexOutputCommand) cmd).addRegexOutputListener(this);
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iex) {
            task.stop();
            Thread.currentThread().interrupt();
            return false;
        }
        failed = failed || (task.getExitStatus() != CommandTask.STATUS_SUCCEEDED);
        if (failed) work = null;
        if (work != null) {
            try {
                fileSystem.setRootDirectory(new java.io.File(work));
                vars.put("ROOTDIR", work);
            } catch (java.beans.PropertyVetoException pvex) {
            } catch (java.io.IOException ioex) {}
            File workDir = new File(work);
            if(workDir.exists()){                
                File[] files = workDir.listFiles();
                if((files.length) == 0){
                    vars.put("DO_PVCS_CHECKOUT","true");    //NOI18N
                }else
                    vars.put("DO_PVCS_CHECKOUT","");        //NOI18N
            }
            
        }
        
        return true;
    }
    
    /**
     * This method is called, with elements of the output data.
     * @param elements the elements of output data.
     */
    public void outputMatchedGroups(String[] elements) {
        if (elements[0] == null || elements[0].length() == 0) return ;
        if (elements[0].indexOf(ERROR) >= 0) {
            failed = true;
            return ;
        }
        work = elements[0];
    }
    
}
