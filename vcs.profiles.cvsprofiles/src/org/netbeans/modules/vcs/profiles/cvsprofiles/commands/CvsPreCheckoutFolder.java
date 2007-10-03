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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 * This class creates CVS/ folder with empty Entries and Repository file
 * and Root file with the current CVSROOT.
 * This is necessary for successful checkout on Windows and checkout
 * by javacvs client, which does not create the CVS folder in the root
 * directory.
 *
 * @author  Martin Entlicher
 */
public class CvsPreCheckoutFolder extends Object implements VcsAdditionalCommand {
    
    private static final String CVS_FOLDER_NAME = "CVS"; // NOI18N
    
    /** Creates a new instance of CvsPreCheckoutFolder */
    public CvsPreCheckoutFolder() {
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                            
        String root = (String) vars.get("CHECKOUT_ROOTDIR"); // For CHECKOUT_IMPORTED global command
        if (root == null) {
            root = (String) vars.get("ROOTDIR");
        }
        String dir = (String) vars.get("DIR");
        String file = (String) vars.get("FILE");
        // Do nothing when we're not in root.
        if (dir != null && dir.length() > 0 &&
            file != null && file.length() > 0) return true;
        File rootDir = new File(root);
        if (!isACVSFolder(rootDir)) {
            String cvsRoot = (String) vars.get("CVSROOT");
            cvsRoot = Variables.expand(vars, cvsRoot, false);
            if (createCVSFolder(rootDir, cvsRoot)) {
                stdoutNRListener.outputLine("CVS folder with basic administrative files created successfully in "+rootDir.getAbsolutePath()+" directory.");
            } else {
                stderrNRListener.outputLine("A problem in creation of CVS folder with basic administrative files in "+rootDir.getAbsolutePath()+" directory.");
            }
        }
        return true;
    }
    
    private static boolean isACVSFolder(File dir) {
        File[] children = dir.listFiles();
        if (children == null) return false;
        boolean is = false;
        for (int i = 0; i < children.length; i++) {
            if (children[i].getName().equals(CVS_FOLDER_NAME)) {
                return true;
            } else {
                if (children[i].isDirectory() && new File(children[i], CVS_FOLDER_NAME).exists()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean createCVSFolder(File root, String cvsRoot) {
        File cvsFolder = new File(root, CVS_FOLDER_NAME);
        if (!cvsFolder.mkdir()) return false;
        File entries = new File(cvsFolder, "Entries");
        if (!writeContent(entries, "D\n")) return false;
        File repository = new File(cvsFolder, "Repository");
        if (!writeContent(repository, ".\n")) return false;
        File rootF = new File(cvsFolder, "Root");
        if (!writeContent(rootF, cvsRoot + '\n')) return false;
        return true;
    }
    
    private static boolean writeContent(File file, String content) {
        Writer w = null;
        try {
            w = new BufferedWriter(new FileWriter(file));
            w.write(content);
        } catch (IOException ioex) {
            return false;
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException ioex) {}
            }
        }
        return true;
    }
    
}
