/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase;

import org.netbeans.modules.clearcase.util.ClearcaseUtils;
import org.netbeans.modules.clearcase.client.*;

import java.io.File;
import java.io.IOException;

/**
 * Now the 'cache' does not cache revisions of files, it fetches them everytime they are needed.
 * 
 * @author Maros Sandor
 */
public class VersionsCache implements NotificationListener {
    
    private static final VersionsCache instance = new VersionsCache();

    public static final String REVISION_BASE = "BASE";
    
    public static final String REVISION_CURRENT = "LOCAL"; // NOI18N
    
    public static final String REVISION_HEAD    = "HEAD"; // NOI18N
    
    private VersionsCache() {
    }
    
    public static VersionsCache getInstance() {
        return instance;
    }

    public File getFileRevision(File workingCopy, String revision) throws IOException {
        return getRemoteFile(workingCopy, revision, false);
    }
    
    public File getRemoteFile(File workingCopy, String revision, boolean beQuiet) throws IOException {
        if (REVISION_CURRENT.equals(revision)) {
            return workingCopy;
        } else if (REVISION_BASE.equals(revision)) {
            revision = Clearcase.getInstance().getFileStatusCache().getInfo(workingCopy).getStatus(workingCopy).getVersionSelector();
            if (revision == null) return null;
        }
        String revisionSpec = ClearcaseUtils.getExtendedName(workingCopy, revision);  
                
        File tempFile = File.createTempFile("nbclearcase-", "get");
        tempFile.delete();
        ClearcaseClient.CommandRunnable cr =Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Getting Clearcased File...",
                new GetCommand(tempFile, revisionSpec, this)));
        cr.waitFinished();
        tempFile.deleteOnExit();
        if (cr.getCommandError() == null && tempFile.isFile()) return tempFile;
        return null;
    }

    public void commandStarted() {
    }

    public void outputText(String line) {
    }

    public void errorText(String line) {
    }

    public void commandFinished() {
    }
}