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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDisplayer;

/**
 * This class checks for modified files (running "echo n | cvs unedit .."),
 * then asks the user whether they want to revert the changes for these files
 * and finally runs "echo y.. | cvs unedit ...".
 *
 * @author  Martin Entlicher
 */
public class CvsUnedit extends Object implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;
    /** Creates new CvsEditStatus */
    public CvsUnedit() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 2) {
            stderrListener.outputLine("The two cvs unedit commands are expected as arguments.\nFirst test command and second a real one."); // NOI18N
            return false;
        }
        boolean stdInput = "-i".equals(args[0]); // NOI18N
        boolean fileInput = "-fi".equals(args[0]); // NOI18N
        if (stdInput || fileInput) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            args = newArgs;
        }
        final StringBuffer buff = new StringBuffer();
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        vars.put("CMD_INPUT", "n"); // NOI18N
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        vce.addOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                buff.append(line);
            }
        });
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            Thread.currentThread().interrupt();
        }
        String output = buff.toString();
        if (output.trim().length() == 0) {
            notifyOfNoChanges();
            return true;
        }
        ArrayList modifiedFiles = getModifiedFiles(output);
        if (!warnOfModifiedFiles(modifiedFiles)) return true;
        cmd = fileSystem.getCommand(args[1]);
        File inputFile = null;
        if (fileInput) {
            try {
                inputFile = createInputFile(getYes(modifiedFiles.size(), ""));
                vars.put("CMD_INPUT_FILE", inputFile.getAbsolutePath());
            } catch (IOException ioex) {
                org.openide.ErrorManager.getDefault().notify(ioex);
            }
        } else if (stdInput) {
            cmd.setProperty(UserCommand.PROPERTY_INPUT, getYes(modifiedFiles.size(), "")); // NOI18N
        } else {
            vars.put("CMD_INPUT", getYes(modifiedFiles.size(), "\\\"")); // NOI18N
        }
        vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            Thread.currentThread().interrupt();
        }
        if (inputFile != null) {
            inputFile.delete();
        }
        return true;
    }
    
    private static final String MODIFIED_FILES = " has been "; // NOI18N
    private static final String REVERT_CHANGES = "; revert changes? "; // NOI18N
    
    private ArrayList getModifiedFiles(String output) {
        ArrayList list = new ArrayList();
        int begin = 0;
        while (begin < output.length()) {
            int end = output.indexOf(MODIFIED_FILES, begin);
            if (end < 0) break;
            list.add(output.substring(begin, end));
            end = output.indexOf(REVERT_CHANGES, end);
            if (end > 0) begin = end + REVERT_CHANGES.length();
            else begin = output.length();
        }
        return list;
    }
    
    private void notifyOfNoChanges() {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
            NbBundle.getMessage(CvsUnedit.class, "MSG_UneditNoChanges")));
    }
    
    private boolean warnOfModifiedFiles(ArrayList modifiedFiles) {
        String[] files = (String[]) modifiedFiles.toArray(new String[0]);
        return NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(CvsUnedit.class,
                                    "MSG_UneditModifiedFilesConfirmation", // NOI18N
                                    VcsUtilities.arrayToQuotedStrings(files)))));
    }
    
    private String getYes(int num, String quote) {
        StringBuffer yes = new StringBuffer();
        yes.append(quote);
        while (num-- > 0) {
            yes.append("y");
            yes.append(System.getProperty("line.separator"));
        }
        yes.append(quote);
        return yes.toString();
    }
    
    private File createInputFile(String content) throws IOException {
        File tmp = File.createTempFile("input", "txt");
        tmp.deleteOnExit();
        OutputStream in = null;
        try {
            in = new BufferedOutputStream(new FileOutputStream(tmp));
            in.write(content.getBytes());
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return tmp;
    }
    
}
