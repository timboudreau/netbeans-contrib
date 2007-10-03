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

import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;

/**
 * Find whether a writable copy exists before get command
 * and provide a warning if true.
 * @author  Martin Entlicher
 */
public class PvcsGetWithQuestions implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;
    /** Creates new PvcsGetWritableCheck */
    public PvcsGetWithQuestions() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private boolean runCommand(String name, Hashtable vars, CommandOutputListener stderrNRListener) {
        VcsCommand cmd = fileSystem.getCommand(name);
        if (cmd == null) {
            stderrNRListener.outputLine("Uknown command '"+name+"'.");
            return false;
        }
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().preprocessCommand(vce, vars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            return false;
        }
        return vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
    }
    
    private boolean checkWritable(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        String dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir = rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
            }
        } else {
            if (module == null) {
                dir = rootDir + File.separator + dir;
            } else {
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        String file = (String) vars.get("FILE");
        dir = dir + File.separator + file;
        File filePath = new File(dir);
        if (filePath.canWrite()) {
            System.out.println("");
            Object confirmation = DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(
                    PvcsGetWithQuestions.class, "Get_Writable_Confirmation", file),
                    NotifyDescriptor.Confirmation.YES_NO_OPTION));
            if (!NotifyDescriptor.YES_OPTION.equals(confirmation)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkLocked(Hashtable vars, String getArguments) {
        if (getArguments != null && getArguments.indexOf("-l") >= 0) {
            String file = (String) vars.get("FILE");
            String path = (String) vars.get("PATH");

            FileProperties fprops = Turbo.getMeta(FileUtil.toFileObject(new File(path)));
            String locker = fprops != null ? fprops.getLocker() : null;
            if (locker != null && locker.length() > 0) {
                Object confirmation = DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(
                        PvcsGetWithQuestions.class, "Get_Lock_Confirmation", file, locker),
                        NotifyDescriptor.Confirmation.YES_NO_OPTION));
                if (!NotifyDescriptor.YES_OPTION.equals(confirmation)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Expect the PVCS get command as the first argument.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (!checkWritable(vars)) return true;
        if (!checkLocked(vars, args.length > 1 ? args[1] : null)) return true;
        boolean state = runCommand(args[0], vars, stderrNRListener);
        return state;
    }
}
