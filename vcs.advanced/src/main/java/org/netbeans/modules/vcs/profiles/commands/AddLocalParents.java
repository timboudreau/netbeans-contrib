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

package org.netbeans.modules.vcs.profiles.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import org.openide.ErrorManager;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Turbo;

import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * A command, that checks whether the parent folders are local and if yes,
 * it calls the add command on them in the hierarchical order.
 *
 * @author  Martin Entlicher
 */
public class AddLocalParents extends Object implements VcsAdditionalCommand {
    
    private CommandExecutionContext execContext;
    
    /** Creates a new instance of AddLocalParents */
    public AddLocalParents() {
    }
    
    /** Set the VCS file system to use to execute commands.
     */
    public void setExecutionContext(CommandExecutionContext execContext) {
        this.execContext = execContext;
        //if (execContext instanceof VcsFileSystem) {
        //    fileSystem = (VcsFileSystem) execContext;
        //}
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 1) {
            stderrListener.outputLine("Too few arguments, expecting a name of a command "+
                                      "that is to be executed to add local parent folders.");
            return false;
        }
        CommandSupport cmdSupp = execContext.getCommandSupport(args[0]);
        if (cmdSupp == null) {
            stderrListener.outputLine("Did not find command '"+args[0]+"'.");
            return false;
        }
        Collection processingFiles = ExecuteCommand.createProcessingFiles(execContext, vars);
        Collection localParents = collectLocalParents(processingFiles);
        if (localParents.size() > 0) {
            Command cmd = cmdSupp.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
            }
            FileObject[] parentsArr = (FileObject[]) localParents.toArray(new FileObject[0]);
            parentsArr = cmd.getApplicableFiles(parentsArr);
            if (parentsArr == null || parentsArr.length == 0) {
                return true; // Nothing to run on.
            }
            cmd.setFiles(parentsArr);
            CommandTask task = cmd.execute();
            try {
                task.waitFinished(0);
            } catch (InterruptedException iex) {
                task.stop();
                Thread.currentThread().interrupt();
            }
            return task.getExitStatus() == task.STATUS_SUCCEEDED;
        } else {
            return true;
        }
    }
    
    private Collection collectLocalParents(Collection processingFiles) {
        Collection localParents = new ArrayList();
        if (execContext instanceof FileSystem) {
            FileSystem fs = ((FileSystem) execContext);
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                FileObject fo = fs.findResource(name);
                if (fo != null) {
                    addLocalParents(fo, localParents);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalStateException("No resource found for '"+name+"'"));
                }
            }
        } else {
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                String fullPath = (String) it.next();
                FileObject fo = FileUtil.toFileObject(new File(fullPath));
                if (fo != null) {
                    addLocalParents(fo, localParents);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalStateException("No resource found for '"+fullPath+"'"));
                }
            }
        }
        return localParents;
    }
    
    private static void addLocalParents(FileObject fo, Collection localParents) {
        FileObject parent = fo.getParent();
        if (parent != null) {
            FileProperties fprops = Turbo.getMeta(parent);
            if (fprops != null && fprops.isLocal()) {
                addLocalParents(parent, localParents);
                localParents.add(parent);
            }
        }
    }
}
