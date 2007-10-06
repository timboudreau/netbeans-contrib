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

package org.netbeans.modules.tasklist.usertasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import net.fortuna.ical4j.model.ValidationException;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 * SaveCookie for .ics files.
 * 
 * @author tl
 */
public class UTSaveCookie implements SaveCookie {
    private DataObject do_;
    private UserTaskList utl;
    
    /** 
     * Creates a new instance of UTSaveCookie 
     * 
     * @param do_ TaskListDataObject
     * @param utl task list
     */
    public UTSaveCookie(DataObject do_, UserTaskList utl) {
        this.do_ = do_;
        this.utl = utl;
    }    

    public void save() throws IOException {
        ICalExportFormat io = new ICalExportFormat();
        
        FileObject file = do_.getPrimaryFile();
        FileLock lock = file.lock();
        try {
            Writer w = new OutputStreamWriter(new BufferedOutputStream(
                    file.getOutputStream(lock)), "UTF-8"); // NOI18N
            try {
                io.writeList(utl, w, false);
            } catch (ParseException e) {
                throw new IOException(e.getMessage());
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            } catch (ValidationException e) {
                throw new IOException(e.getMessage());
            } finally {
                try {
                    w.close();
                } catch (IOException e) {
                    UTUtils.LOGGER.log(Level.WARNING, 
                            "failed closing file", e); // NOI18N
                }
            }
        } finally {
            lock.releaseLock();
        }

        // Remove permissions for others on the file when on Unix
        // varieties
        if (new File("/bin/chmod").exists()) { // NOI18N
            try {
                Runtime.getRuntime().exec(
                     new String[] {"/bin/chmod", "go-rwx",  // NOI18N
                         FileUtil.toFile(file).getAbsolutePath()});
            } catch (Exception e) {
                // Silently accept
                UTUtils.LOGGER.log(Level.INFO, 
                        "chmod call failed", e); // NOI18N
            }
        }

        do_.setModified(false);
    }
}
