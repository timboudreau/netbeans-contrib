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

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.openide.filesystems.FileUtil;

/**
 * After Import and before Checkout is executed, it's necessary to move
 * the imported sources to a backup directory.
 *
 * @author  Martin Entlicher
 */
public class MoveImportedFolder extends Object implements VcsAdditionalCommand {
    
    private File createOrig(File root, File[] files) {
        String orig = "ORIG";
        boolean exists = true;
        int i = 0;
        while (exists) {
            exists = false;
            for (int j = 0; j < files.length; j++) {
                if (orig.equalsIgnoreCase(files[j].getName())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                orig = "ORIG"+i;
            }
        }
        File origFolder = new File(root, orig);
        origFolder.mkdir();
        return origFolder;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {

        String rootPath = (String) vars.get("ROOTDIR");
        String checkoutPath = (String) vars.get("CHECKOUT_ROOTDIR");
        String reposDir = (String) vars.get("REPOS_DIR");
        File root = FileUtil.normalizeFile(new File(rootPath));
        File checkoutRoot = FileUtil.normalizeFile(new File(checkoutPath, reposDir));
        if (!root.equals(checkoutRoot)) {
            // Nothing will be moved when the checkout folder is different
            vars.put("IMPORTED_ORIG_FOLDER", root.getAbsolutePath());
            return true;
        }
        File[] files = root.listFiles();
        File orig = createOrig(root, files);
        for (int i = 0; i < files.length; i++) {
            files[i].renameTo(new File(root, orig.getName() + File.separator + files[i].getName()));
        }
        vars.put("IMPORTED_ORIG_FOLDER", orig.getAbsolutePath());
        return true;
    }
}
