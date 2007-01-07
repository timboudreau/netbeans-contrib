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
